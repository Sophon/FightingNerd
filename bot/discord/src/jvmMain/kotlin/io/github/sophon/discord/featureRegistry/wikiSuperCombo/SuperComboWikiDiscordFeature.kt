package io.github.sophon.discord.featureRegistry.wikiSuperCombo

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.SlashCommand
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.GetMoveUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.SearchCharacterDataUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.SyncSuperComboDataUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.discord.util.createErrorEmbed
import io.github.sophon.discord.util.field
import io.github.sophon.discord.util.orClickable
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
    override val mainCommand: Command = Command.FDSF6
    override val featureInfo = FeatureInfo(
        name = "SuperCombo Wiki",
        url = "https://wiki.supercombo.gg/",
        iconUrl = "https://i.imgur.com/aW5ys7q.png",
    )
    override val slashCommands = listOf(
        SlashCommand(
            name = Command.FDSF6,
            description = "SF6 frame data",
            arguments = listOf(
                SlashCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SlashCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move input"
                )
            )
        ),
        SlashCommand(
            name = Command.CHARSF6,
            description = "SF6 character data",
            arguments = listOf(
                SlashCommand.Argument(
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
        vararg args: String
    ): EmbedBuilder.() -> Unit {
        return when (command) {
            Command.CHARSF6 -> searchCharacter(*args)
            Command.FDSF6 -> searchMove(*args)
            else -> createErrorEmbed(BotError.BOT_LOGIC_ERROR)
        }
    }

    private suspend fun syncData(): EmptyResult<BotError> {
        return syncDataUseCase.invoke()
    }

    private suspend fun searchCharacter(
        vararg args: String
    ): EmbedBuilder.() -> Unit {
        val result = searchCharacterDataUseCase.invoke(args.joinToString(" "))

        return when (result) {
            is Result.Success -> createCharacterEmbed(result.data)
            is Result.Error -> createErrorEmbed(result.error)
        }
    }

    private suspend fun searchMove(
        vararg args: String,
    ): EmbedBuilder.() -> Unit {
        val result = getMoveUseCase.invoke(args.joinToString(" "))
        return when (result) {
            is Result.Success -> createMoveEmbed(move = result.data)
            is Result.Error -> createErrorEmbed(error = result.error)
        }
    }

    private fun createCharacterEmbed(
        character: Character
    ): EmbedBuilder.() -> Unit = {
        title = character.displayName
        url = character.wikiUrl
        color = Color(ORANGE)

        character.sf6Properties?.let { properties ->
            field(name = "Walk (b|f)", value = "${properties.fwdWalkSpd} | ${properties.bwdWalkSpd}")
            field(name = "Dash (b|f)", value = "${properties.fwdDashSpd} | ${properties.bwdDashSpd}")
            field(name = "Dash dist (b|f)", value = "${properties.fwdDashDist} | ${properties.bwdDashDist}")

            field(name = "R (mn, bl, mx)", value = "${properties.dRushMin}, ${properties.dRushBlock}, ${properties.dRushMax}")

            field {
                name = "Additional Properties"
                value = listOf(
                    "• ❤️ HP: ${properties.hp}",
                    "• 🤝 Throw range: ${properties.throwRange}",
                    "• 🤝 Throw hurtbox: ${properties.throwHurtbox}",
                    "• Jump Speed: ${properties.jumpSpd}",
                    "• Jump Apex: ${properties.jumpApex}",
                    "• Jump dist (b|f): ${properties.bwdJumpDist} | ${properties.fwdJumpDist}"
                ).joinToString("\n")
                inline = false
            }
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

        field(name = "SU", value = move.startup)
        field(name = "OH", value = move.onHit.orDash())
        field(name = "OB", value = move.onBlock.orDash())
        field(name = "LVL", value = move.sf6Properties?.guard.orDash())

        field(name = "DMG", value = move.damage.orDash())

        createNotes(move)
    }

    private fun EmbedBuilder.createNotes(move: Move) {
        return field(
            name = "📝 NOTES",
            value = move.notes
                .joinToString(separator = "") { note -> "* $note\n" },
            inline = false,
        )
    }

    private companion object {
        private const val TAG = "SuperComboFeature"
        private const val KEY_CHAR_NAME = "character"
        private const val KEY_MOVE = "move"
        const val ORANGE = 0x00FF6A01
    }
}