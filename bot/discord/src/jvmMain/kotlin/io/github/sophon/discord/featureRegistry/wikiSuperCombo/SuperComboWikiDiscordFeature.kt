package io.github.sophon.discord.featureRegistry.wikiSuperCombo

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
import io.github.sophon.core.util.truncate
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.MAX_LENGTH_EMBED
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.featureRegistry.BotOutput
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.Scheduler
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.wikiSuperCombo.domain.SuperComboFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class SuperComboWikiDiscordFeature(
    superComboFeatureInfo: SuperComboFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo = superComboFeatureInfo.featureInfo
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
            command = Command.FDSF6,
            description = "SF6 frame data",
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
            command = Command.CHARSF6,
            description = "SF6 character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            )
        ),
        SupportedCommand(
            command = Command.CHARMK1,
            description = "MK1 character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            ),
        ),
        SupportedCommand(
            command = Command.FDMK1,
            description = "MK1 frame data",
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
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(
        enabledGames: List<Game>
    ) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }

        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.SuperCombo.id)) {
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

    //TODO: this should definitely be a usecase
    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.FD -> {
                var lastError: BotError? = null
                wikis.values.forEach { wiki ->
                    when (val result = searchMove(wiki, query)) {
                        is Result.Success -> return result
                        is Result.Error -> lastError = result.error
                    }
                }

                Result.Error(lastError ?: BotError.UnknownMove(query))
            }
            Command.CHARSF6 -> {
                val wiki = wikis[Game.StreetFighter6.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            Command.FDSF6 -> {
                val wiki = wikis[Game.StreetFighter6.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.CHARMK1 -> {
                val wiki = wikis[Game.MK1.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            Command.FDMK1 -> {
                val wiki = wikis[Game.MK1.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }

    private suspend fun syncData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki, charName = query)
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
                BotOutput(
                    embedBuilder = createMoveEmbed(move),
                    images = if (move.urls.hitboxImageList.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = move.urls.hitboxImageList,
                        )
                    }
                )
            }
    }

    private fun createCharacterEmbed(
        character: Character,
        fastestMoveList: List<Move>,
    ): EmbedBuilder.() -> Unit = {
        title = character.displayName
        url = character.wikiUrl
        color = Color(ORANGE)

        character.images?.iconUrl?.let { iconUrl ->
            thumbnail { url = iconUrl }
        }

        character.sf6Properties?.let { properties ->
            val moves = fastestMoveList.joinToString(", ") { move ->
                move.input
            }
            val startup = fastestMoveList.first().startup.orDash()

            mandatoryField(
                name = "BASIC",
                value = listOf(
                    "* **Fastest normal ($startup)**: $moves",
                    "* ❤️ **HP**: ${properties.hp}",
                    "* 🤝 **Throw range | hurtbox**: ${properties.throwRange} | ${properties.throwHurtbox}",
                ).joinToString("\n"),
                inline = false
            )

            mandatoryField(
                name = "DRIVE",
                value = buildString {
                    appendLine("* **DR distance min**: ${properties.dRushMin}")
                    appendLine("* **DR distance block**: ${properties.dRushBlock}")
                    appendLine("* **DR distance max**: ${properties.dRushMax}")
                },
                inline = false,
            )

            mandatoryField(
                name = "MOVEMENT",
                value = buildString {
                    appendLine("* **Walk speed**: ←${properties.bwdWalkSpd} | ${properties.fwdWalkSpd}→")
                    appendLine("* **Dash speed**: ←${properties.bwdDashSpd} | ${properties.fwdDashSpd}→")
                    appendLine("* **Dash distance**: ←${properties.bwdDashDist} | ${properties.fwdDashDist}→")
                    appendLine("* **Jump distance**: ↖ ${properties.bwdJumpDist} | ${properties.fwdJumpDist} ↗")
                    appendLine("* **Jump apex | speed**: ${properties.jumpApex} | ${properties.jumpSpd}")
                },
                inline = false,
            )
        }

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
        color = Color(ORANGE)
        move.urls.characterImage?.let { thumbnail { url = it } }

        move.urls.hitboxImageList
            .takeIf { it.size == 1 }
            ?.let { image = it.first() }

        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Hit", value = move.onHit)
        mandatoryField(name = "Block", value = move.onBlock)
        mandatoryField(name = "Active", value = move.active)
        mandatoryField(name = "Guard", value = move.guard)
        mandatoryField(name = "Recovery", value = move.recovery)

        optionalField(name = "Damage", value = move.damage)
        optionalField(name = "Invul", value = move.invulnerability)

        sf6Fields(move)
        mk1Fields(move)

        createDetails(move)
        createNotes(move)

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }

    private fun EmbedBuilder.sf6Fields(move: Move) {
        optionalField(name = "JUG start", value = move.sf6Properties?.jugStart)
        optionalField(name = "JUG limit", value = move.sf6Properties?.jugLimit)
        optionalField(name = "JUG inc", value = move.sf6Properties?.jugIncrease)

        optionalField(name = "Cancel", move.cancel)
        optionalField(name = "Range", move.sf6Properties?.attackRange)
        optionalField(name = "Proj spd", move.sf6Properties?.projectileSpeed)
    }

    private fun EmbedBuilder.mk1Fields(move: Move) {
        optionalField(
            name = "Cost",
            value = move.mkProperties?.cost?.joinToString("; ")
        )
        optionalField(
            name = "Chip",
            value = move.mkProperties?.chip,
        )
        optionalField(
            name = "Flawless block",
            value = move.mkProperties?.flawlessBlockAdv,
        )

        if (move.mkProperties?.hitCancelAdv != null || move.mkProperties?.blockCancelAdv != null) {
            optionalField(
                name = "Cancel hit | block",
                value = "${move.mkProperties?.hitCancelAdv} | ${move.mkProperties?.blockCancelAdv}",
            )
        }
        optionalField(
            name = "Punish",
            value = move.mkProperties?.punish,
        )
    }

    private fun EmbedBuilder.createNotes(move: Move) {
        return optionalField(
            name = "📝 NOTES",
            value = move.notes
                .emojify()
                .joinToString(separator = "") { note -> "* $note\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )
    }

    private fun EmbedBuilder.createDetails(move: Move) {
        val properties = move.sf6Properties ?: return

        if (
            properties.run {
                DROH == null && DROB == null
                        && DRcOH == null && DRcOB == null
                        && driveDmgOnHit == null && driveDmgOnBlock == null
                        && driveGain == null
                        && superGainOnHit == null && superGainOnBlock == null
            }
        ) return

        mandatoryField(
            name = "⭐️ DRIVE & SUPER",
            value = buildString {
                if (properties.DROH != null || properties.DROB != null) {
                    appendLine("* **DR (OH | OB)**: ${properties.DROH.orDash()} | ${properties.DROB.orDash()}")
                }

                if (properties.DRcOH != null || properties.DRcOB != null) {
                    appendLine("* **DRc (OH | OB)**: ${properties.DRcOH.orDash()} | ${properties.DRcOB.orDash()}")
                }

                if (properties.driveDmgOnHit != null || properties.driveDmgOnBlock != null) {
                    appendLine("* **Drive damage (OH | OB)**: ${properties.driveDmgOnHit.orDash()} | ${properties.driveDmgOnBlock.orDash()}")
                }

                if (properties.driveGain != null) {
                    appendLine("* **Drive gain**: ${properties.driveGain}")
                }

                if (properties.superGainOnHit != null || properties.superGainOnBlock != null) {
                    append("* **SUP gain (OH | OB)**: ${properties.superGainOnHit.orDash()} | ${properties.superGainOnBlock.orDash()}")
                }
            }
                .trim()
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )
    }

    private fun List<String>.emojify(): List<String> {
        return buildList {
            this@emojify.forEach { note ->
                val emojified = buildString {
                    if (note.contains("invincibility", ignoreCase = true)) append("🛡️ ")
                    if (note.contains("juggle", ignoreCase = true)) append("🤹 ")
                    if (note.contains("rhythm", ignoreCase = true)) append("🥁 ")
                    if (note.contains("scaling", ignoreCase = true)) append("📉 ")
                    if (note.contains("shimmy", ignoreCase = true)) append("💃 ")
                    if (note.contains("throw", ignoreCase = true)) append("🤝 ")
                    append(note)
                }
                add(emojified)
            }
        }
    }


    private companion object {
        const val TAG = "SuperComboWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val ORANGE = 0x00FF6A01
    }
}