package io.github.sophon.discord.featureRegistry.wikiMizuumi

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.domain.Scheduler
import io.github.sophon.discord.domain.SupportedCommand
import io.github.sophon.discord.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.domain.Source
import io.github.sophon.wikimizuumi.MizuumiFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class MizuumiWikiDiscordFeature(
    mizuumiFeatureInfo: MizuumiFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val createMizuumiMoveEmbedUseCase: CreateMizuumiMoveEmbedUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo = mizuumiFeatureInfo.featureInfo
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
            command = Command.FDMB,
            description = "MBTL frame data",
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
            command = Command.ALIASMB,
            description = "MBTL character aliases",
        ),
        SupportedCommand(
            command = Command.FDUNI,
            description = "Uni2 frame data",
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
            command = Command.CHARUNI,
            description = "Uni2 character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            ),
        ),
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }

        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.Mizuumi.id)) {
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

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.FD -> {
                var lastError: BotError? = null
                for ((gameId, wiki) in wikis) {
                    val game = Game.fromId(gameId)
                    if (game == null) {
                        Result.Error(lastError ?: BotError.UnknownMove(query))
                    } else {
                        when (val result = searchMove(wiki, query, game)) {
                            is Result.Success -> return result
                            is Result.Error -> lastError = result.error
                        }
                    }
                }
                Result.Error(lastError ?: BotError.UnknownMove(query))
            }
            Command.FDMB -> {
                val game = Game.MBTL
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query, game)
            }
            Command.ALIASMB -> {
                val game = Game.MBTL
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                getCharacterAliases(wiki)
            }
            Command.FDUNI -> {
                val game = Game.Uni2
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query, game)
            }
            Command.CHARUNI -> {
                val game = Game.Uni2
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
        game: Game,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()
                val (primary, full) = createMizuumiMoveEmbedUseCase
                    .invoke(move, game, featureInfo)
                val buttons = if (full == null) {
                    null
                } else {
                    listOf(
                        BotOutput.EmbedButton(label = "Details", action = BotOutput.EmbedButton.Action.Edit())
                    )
                }

                BotOutput(
                    primaryEmbedBuilder = primary,
                    fullEmbedBuilder = full,
                    buttons = buttons?.let { BotOutput.ButtonSet(buttonList = it) },
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

    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki, query)
            .map { (character, fastestMoveList) ->
                BotOutput(
                    primaryEmbedBuilder = getCharacterEmbedBuilder(character, fastestMoveList)
                )
            }
    }

    private suspend fun getCharacterAliases(wiki: WikiClient): Result<BotOutput, BotError> {
        return createCharacterAliasesEmbedUseCase.invoke(wiki, featureInfo, TEAL)
            .map { BotOutput(primaryEmbedBuilder = it) }
    }

    private fun getCharacterEmbedBuilder(
        character: Character,
        fastestMoveList: List<Move>,
    ): EmbedBuilder.() -> Unit = {
        title = character.displayName
        url = character.wikiUrl
        color = Color(TEAL)
        character.images?.iconUrl?.let { iconUrl ->
            thumbnail { url = iconUrl }
        }

        val moves = fastestMoveList.joinToString(", ") { it.input }
        val startup = fastestMoveList.first().startup.orDash()
        mandatoryField(
            name = "Fastest normal",
            value = "${startup}f: $moves"
        )
        mandatoryField(name = "HP", character.uni2Properties?.hp)
        mandatoryField(
            name = "Umo",
            value = if (character.umo.size == 1) {
                character.umo.toString()
            } else {
                character.umo.joinToString {
                    "- $it\n"
                }
            },
        )

        character.uni2Properties?.apply {
            optionalField(name = "Jump", value = "**$jumpStartup** ($jumpDuration)")

            val walkValue = buildString {
                bWalkSpeed?.let { append("← **$it**") }
                fWalkSpeed?.let { append(" → **$it** ") }
            }
            optionalField(name = "Walk", value = walkValue)

            val bDashValue = buildString {
                bDashStartup?.let { append("**${it}f**") }
                bDashDuration?.let { append(" - dur: $it") }
                bDashDistance?.let { append(" dist: $it\n") }
                append("Inv: **$bDashFullInvulStart - $bDashFullInvulEnd**")
                append(" Thr: **$bDashThrowInvulStart - $bDashThrowInvulEnd**")
            }
            optionalField(name = "bDash", value = bDashValue)

            optionalField(name = "Vorpal", value = character.uni2Properties?.vorpalTrait)
        }

        optionalField(
            name = "Trait",
            value = character.uni2Properties?.trait,
            inline = false,
        )

        featureFooter(featureInfo)
    }


    private companion object {
        const val TAG = "MizuumiWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val TEAL = 0x0007A9F5
    }
}