package io.github.sophon.discord.featureRegistry.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.truncate
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.util.getLevel
import io.github.sophon.discord.BotError
import io.github.sophon.discord.MAX_LENGTH_EMBED
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.domain.Scheduler
import io.github.sophon.discord.domain.SupportedCommand
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.GetCharactersUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.domain.Source
import io.github.sophon.wikidustloop.domain.DustLoopFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class DustLoopWikiDiscordFeature(
    dustLoopFeatureInfo: DustLoopFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dustLoopFeatureInfo.featureInfo
    override val defaultCommand = SupportedCommand(
        command = Command.FD,
        description = "Global frame data",
        arguments = listOf(
            SupportedCommand.Argument(
                name = KEY_CHAR_NAME,
                description = "Character name",
            ),
            SupportedCommand.Argument(
                name = KEY_MOVE,
                description = "Move input"
            )
        )
    )
    override val otherCommands = listOf(
        SupportedCommand(
            command = Command.CHARGG,
            description = "GGST character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            )
        ),
        SupportedCommand(
            command = Command.FDGG,
            description = "GGST frame data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SupportedCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move input"
                )
            )
        ),
        SupportedCommand(
            command = Command.CHARDB,
            description = "DBFZ character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            ),
        ),
        SupportedCommand(
            command = Command.FDDB,
            description = "DBFZ frame data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SupportedCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move input"
                )
            ),
        ),
        SupportedCommand(
            command = Command.ALIAS,
            description = "Character aliases",
        ),
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }
        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.DustLoop.id)) {
                parametersOf(
                    game.id,
                    InMemoryCharacterListDB(),
                    InMemoryMoveListDB(),
                )
            }
        }
    }

    override suspend fun start() {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }

        scheduler.start(
            task = ::syncData,
        ).onEach { result ->
            result.onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    //TODO: extract to usecase
    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.FD -> {
                var lastError: BotError? = null
                for ((_, wiki) in wikis) {
                    when (val result = searchMove(wiki, query)) {
                        is Result.Success -> return result
                        is Result.Error -> lastError = result.error
                    }
                }
                Result.Error(lastError ?: BotError.UnknownMove(query))
            }
            Command.CHARGG -> {
                val wiki = wikis[Game.GGST.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            Command.FDGG -> {
                val wiki = wikis[Game.GGST.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.CHARDB -> {
                val wiki = wikis[Game.DBFZ.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            Command.FDDB -> {
                val wiki = wikis[Game.DBFZ.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.CHARGB -> {
                val wiki = wikis[Game.GBVSR.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            Command.FDGB -> {
                val wiki = wikis[Game.GBVSR.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.ALIAS -> {
                val wiki = wikis[Game.DBFZ.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                createAliases(wiki)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki = wiki, charName = query)
            .map { (character, fastestMoveList) ->
                BotOutput(embedBuilder = createCharacterEmbed(character, fastestMoveList))
            }
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: move.urls.moveImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()

                BotOutput(
                    embedBuilder = createMoveEmbed(move),
                    images = if (images.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = images,
                        )
                    }
                )
            }
    }

    private suspend fun createAliases(wiki: WikiClient): Result<BotOutput, BotError> {
        return getCharactersUseCase.invoke(wiki)
            .map { characterList ->
                BotOutput(
                    embedBuilder = createAliasesEmbed(characterList)
                )
            }
    }

    private fun createCharacterEmbed(
        character: Character,
        fastestMoveList: List<Move>,
    ): EmbedBuilder.() -> Unit = {
        title = character.displayName
        url = character.wikiUrl
        color = Color(RED)

        character.images?.iconUrl?.let { iconUrl ->
            thumbnail { url = iconUrl }
        }

        val properties = character.ggstProperties
        val moves = fastestMoveList.joinToString(", ") { it.input }
        val startup = fastestMoveList.first().startup.orDash()

        mandatoryField(
            name = "⭐️ CORE",
            value = buildList {
                add("* **Fastest normal →** $moves ($startup)")
                add("* **Defense →** ${properties?.defense}")
                add("* **Guts →** ${properties?.guts}")
                add("* **Guard balance →** ${properties?.guardBalance}")
                add("* **Boost ATT | DEF** → ${properties?.boostAttack} | ${properties?.boostDefense}")
            }.joinToString("\n"),
            inline = false,
        )

        mandatoryField(
            name = "👟 MOVEMENT",
            value = buildList {
                properties?.umo?.takeIf { it.isNotEmpty() }?.let { umo ->
                    if (umo.size == 1) {
                        add("* **Unique movement →** ${umo.first()}")
                    } else {
                        add("* **Unique movement →** ")
                        umo.forEach { add("   * $it") }
                    }
                }
                add("* **Backdash →** ${properties?.bwdDash}")
                add("   * **Distance →** ${properties?.bwdDashDist}")
                add("   * **Duration →** ${properties?.bwdDashDuration}")
                add("   * **Invulnerability →** ${properties?.bwdDashInvulnerability}")
                properties?.fwdDash?.let { add("* **Forward dash →** $it") }
                add("* **Initial speed →** ${properties?.dashInitialSpd}")
                properties?.dashAcceleration?.let { add("* **Acceleration →** $it") }
                properties?.movementTension?.let { add("* **Tension →** $it") }
                properties?.dashFriction?.let { add("* **Friction →** $it") }
                add("* **Walk →** ← ${properties?.walkSpd} | ${properties?.bwdWalkSpd} →")
            }.joinToString("\n"),
            inline = false,
        )

        mandatoryField(
            name = "🦘 JUMP",
            value = buildList {
                add("* **Prejump →** ${properties?.prejump}")
                add("* **Height (high) →** ${properties?.jumpHeight} (${properties?.highJumpHeight})")
                add("* **Duration (high) →** ${properties?.jumpDuration} (${properties?.highJumpDuration})")
                add("* **Gravity (high) →** ${properties?.jumpGravity} (${properties?.highJumpGravity})")
                properties?.jumpTension?.let { add("* **Tension →** $it") }
            }.joinToString("\n"),
            inline = false,
        )

        mandatoryField(
            name = "💨 AIRDASH",
            value = buildList {
                add("* **IAD →** ${properties?.earliestIAD}")
                add("* **Distance | Duration →** ${properties?.adDist} | ${properties?.adDuration}")
                add("* **B Distance | Duration →** ${properties?.abdDist} | ${properties?.abdDuration}")
                properties?.airDashTension?.let { add("* **Tension →** $it") }
            }.joinToString("\n"),
            inline = false,
        )

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }

    private fun createMoveEmbed(
        move: Move,
    ): EmbedBuilder.() -> Unit = {
        title = move.input
        url = move.urls.wikiUrl
        description = "**${move.charName}**: ${move.name.orEmpty()}"
        color = Color(RED)
        move.urls.characterImage?.let { thumbnail { url = it } }

        val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
            ?: move.urls.moveImageList.takeIf { it.isNotEmpty() }
            ?: emptyList()

        images
            .takeIf { it.size == 1 }
            ?.let { image = it.first() }

        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Hit", value = move.onHit)
        mandatoryField(name = "Block", value = move.onBlock)
        mandatoryField(name = "Active", value = move.active)
        mandatoryField(name = "Guard", value = move.guard)
        mandatoryField(name = "Recovery", value = move.recovery)

        optionalField(name = "Damage", value = move.damage)
        optionalField(name = "Invulnerability", value = move.invulnerability)
        optionalField(name = "Counter", value = move.onCH)

        optionalField(name = "Level", value = move.getLevel())
        optionalField(name = "Risc gain", value = move.ggstProperties?.riscGain)
        optionalField(name = "Risc loss", value = move.ggstProperties?.riscLoss)
        optionalField(name = "Cancel", value = move.ggstProperties?.cancel)
        optionalField(name = "Prorate", value = move.ggstProperties?.prorate)
        optionalField(name = "Input tension", value = move.ggstProperties?.inputTension)
        optionalField(name = "Chip", value = move.ggstProperties?.chipRatio)

        optionalField(name = "Attribute", value = move.dbfzProperties?.attribute)
        optionalField(name = "Smash", value = move.dbfzProperties?.smash)
        optionalField(name = "Ki gain", value = move.dbfzProperties?.kiGain)
        optionalField(name = "Prorate", value = move.dbfzProperties?.prorate)
        optionalField(name = "BlockStun", value = move.dbfzProperties?.blockStun)
        if (move.dbfzProperties?.groundHit != null || move.dbfzProperties?.airHit != null) {
            optionalField(
                name = "Hit (ground | air)",
                value = "${move.dbfzProperties?.groundHit} | ${move.dbfzProperties?.airHit}"
            )
        }
        optionalField(name = "Type", value = move.dbfzProperties?.type)
        optionalField(name = "Level", value = move.dbfzProperties?.level)

        createNotes(move)

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }

    private fun EmbedBuilder.createNotes(move: Move) = optionalField(
        name = "📝 NOTES",
        value = move.notes
            .joinToString(separator = "") { note -> "* $note\n" },
        inline = false,
    )

    private fun createAliasesEmbed(
        characterList: List<Character>,
    ): EmbedBuilder.() -> Unit = {
        val string = buildString {
            characterList
                .filter { it.aliasList.isNotEmpty() }
                .forEach { character ->
                    val aliases = character.aliasList.joinToString(", ")
                    append("- **${character.displayName}** → $aliases\n")
                }
        }

        mandatoryField(
            name = "🥸 CHARACTER ALIASES",
            value = string,
        )
    }


    private companion object {
        const val TAG = "DustLoopWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val RED = 0x00950117
    }
}