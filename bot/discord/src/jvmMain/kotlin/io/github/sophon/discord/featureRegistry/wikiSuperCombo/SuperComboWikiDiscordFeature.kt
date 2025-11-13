package io.github.sophon.discord.featureRegistry.wikiSuperCombo

import MAX_LENGTH_EMBED
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.truncate
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.GetMoveUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.SearchCharacterDataUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.SyncSuperComboDataUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.discord.util.separator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.hours

internal class SuperComboWikiDiscordFeature(
    private val syncDataUseCase: SyncSuperComboDataUseCase,
    private val searchCharacterDataUseCase: SearchCharacterDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature {
    override val defaultCommand = SupportedCommand(
        command = Command.FD,
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
    )
    override val featureInfo = FeatureInfo(
        name = "SuperCombo Wiki",
        url = "https://wiki.supercombo.gg/",
        iconUrl = "https://i.imgur.com/aW5ys7q.png",
    )
    override val otherCommands = listOf(
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

    override suspend fun start() {
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
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return when (command) {
            Command.FD -> searchCharacter(query)
            Command.CHARSF6 -> searchMove(query)
            else -> Result.Error(BotError.BOT_LOGIC_ERROR)
        }
    }

    private suspend fun syncData(): EmptyResult<BotError> {
        return syncDataUseCase.invoke()
    }

    private suspend fun searchCharacter(
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return searchCharacterDataUseCase.invoke(charName = query)
            .map { createCharacterEmbed(it) }
    }

    private suspend fun searchMove(
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return getMoveUseCase.invoke(query)
            .map { createMoveEmbed(it) }
    }

    private fun createCharacterEmbed(
        character: Character
    ): EmbedBuilder.() -> Unit = {
        title = character.displayName
        url = character.wikiUrl
        color = Color(ORANGE)

        character.sf6Properties?.let { properties ->
            mandatoryField(
                name = "Walk (b|f)",
                value = "${properties.fwdWalkSpd} | ${properties.bwdWalkSpd}"
            )
            mandatoryField(
                name = "Dash (b|f)",
                value = "${properties.fwdDashSpd} | ${properties.bwdDashSpd}"
            )
            mandatoryField(
                name = "Dash dist (b|f)",
                value = "${properties.fwdDashDist} | ${properties.bwdDashDist}")

            mandatoryField(
                name = "R (mn, bl, mx)",
                value = "${properties.dRushMin}, ${properties.dRushBlock}, ${properties.dRushMax}"
            )

            mandatoryField(
                name = "Additional Properties",
                value = listOf(
                    "• ❤️ HP: ${properties.hp}",
                    "• 🤝 Throw range: ${properties.throwRange}",
                    "• 🤝 Throw hurtbox: ${properties.throwHurtbox}",
                    "• Jump Speed: ${properties.jumpSpd}",
                    "• Jump Apex: ${properties.jumpApex}",
                    "• Jump dist (b|f): ${properties.bwdJumpDist} | ${properties.fwdJumpDist}"
                ).joinToString("\n"),
                inline = false
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
        mandatoryField(name = "LVL", value = move.sf6Properties?.guard)
        mandatoryField(name = "Active", value = move.sf6Properties?.active)
        mandatoryField(name = "Recovery", value = move.recovery)

        optionalField(name = "DMG", value = move.damage)
        optionalField(name = "Invul", value = move.sf6Properties?.invulnerability)

        optionalField(name = "JUGst", value = move.sf6Properties?.jugStart)
        optionalField(name = "JUGlim", value = move.sf6Properties?.jugLimit)
        optionalField(name = "JUG++", value = move.sf6Properties?.jugIncrease)

        separator()

        optionalField(
            name = "DR (OH | OB)",
            values = listOf(
                move.sf6Properties?.DROH,
                move.sf6Properties?.DROB,
            )
        )
        optionalField(
            name = "DRc (OH | OB)",
            values = listOf(
                move.sf6Properties?.DRcOH,
                move.sf6Properties?.DRcOB,
            )
        )
        optionalField(
            name = "DR dmg (OH | OB)",
            values = listOf(
                move.sf6Properties?.driveDmgOnHit,
                move.sf6Properties?.driveDmgOnBlock,
            )
        )
        optionalField(name = "DR++", value = move.sf6Properties?.driveGain)

        optionalField(
            name = "SUP++ (OH | OB)",
            values = listOf(
                move.sf6Properties?.superGainOnHit,
                move.sf6Properties?.superGainOnBlock
            )
        )

        separator()

        optionalField(name = "Cancel", move.sf6Properties?.cancel)
        optionalField(name = "Range", move.sf6Properties?.attackRange)
        optionalField(name = "Proj spd", move.sf6Properties?.projectileSpeed)

        createNotes(move)
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
        const val TAG = "SuperComboFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val ORANGE = 0x00FF6A01
    }
}