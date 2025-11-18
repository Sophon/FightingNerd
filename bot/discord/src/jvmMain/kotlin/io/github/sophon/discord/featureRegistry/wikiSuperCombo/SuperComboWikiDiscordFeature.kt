package io.github.sophon.discord.featureRegistry.wikiSuperCombo

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
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
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.GetSuperComboFeatureInfoUseCase
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.hours

internal class SuperComboWikiDiscordFeature(
    getSuperComboFeatureInfoUseCase: GetSuperComboFeatureInfoUseCase,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo = getSuperComboFeatureInfoUseCase.invoke()
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
        )
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(
        enabledGames: List<String>
    ) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGames
        }

        supportedGames.forEach { gameId ->
            wikis[gameId] = get {
                parametersOf(
                    gameId,
                    InMemoryCharacterListDB(),
                    InMemoryMoveListDB(),
                )
            }
        }
    }

    override suspend fun start() {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }

        scheduler.start(
            period = 1.hours,
            task = ::syncData,
        ).onEach { result ->
            result.onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<BotOutput, BotError> {
        val gameId = when (command) {
            Command.FD,
            Command.CHARSF6,
            Command.FDSF6,
                -> "Street_Fighter_6"
//            Command.FDMK1 -> "Mortal_Kombat_1"
            else -> {
                val error = BotError.BotLogicError(command.name, query)
                return Result.Error(error)
            }

        }

        val wiki = wikis[gameId]
            ?: return Result.Error(BotError.UnsupportedGame(query))

        return when (command) {
            Command.FD,
            Command.FDSF6,
                -> searchMove(wiki, query)

            Command.CHARSF6 -> searchCharacter(wiki, query)
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
            .map { BotOutput(embedBuilder = createMoveEmbed(it)) }
    }

    private fun createCharacterEmbed(
        character: Character,
        fastestMoveList: List<Move>,
    ): EmbedBuilder.() -> Unit = {
        title = character.displayName
        url = character.wikiUrl
        color = Color(ORANGE)

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
        description = "**${move.charName}**: ${move.name}"
        color = Color(ORANGE)

        mandatoryField(name = "SU", value = move.startup)
        mandatoryField(name = "OH", value = move.onHit)
        mandatoryField(name = "OB", value = move.onBlock)
        mandatoryField(name = "Act", value = move.sf6Properties?.active)
        mandatoryField(name = "LVL", value = move.sf6Properties?.guard)
        mandatoryField(name = "Rec", value = move.recovery)

        optionalField(name = "DMG", value = move.damage)
        optionalField(name = "Invul", value = move.sf6Properties?.invulnerability)

        optionalField(name = "JUGst", value = move.sf6Properties?.jugStart)
        optionalField(name = "JUGlim", value = move.sf6Properties?.jugLimit)
        optionalField(name = "JUG++", value = move.sf6Properties?.jugIncrease)

        createDetails(move)

        optionalField(name = "Cancel", move.sf6Properties?.cancel)
        optionalField(name = "Range", move.sf6Properties?.attackRange)
        optionalField(name = "Proj spd", move.sf6Properties?.projectileSpeed)

        createNotes(move)

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
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