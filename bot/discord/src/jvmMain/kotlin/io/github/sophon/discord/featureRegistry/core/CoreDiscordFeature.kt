package io.github.sophon.discord.featureRegistry.core

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_LONG_S
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.domain.SupportedCommand
import io.github.sophon.discord.featureRegistry.FeatureRegistry
import io.github.sophon.discord.featureRegistry.admin.adminCommands
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.Source
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

internal class CoreDiscordFeature(
    getBotFeatureInfoUseCase: GetBotFeatureInfoUseCase,
): DiscordRegisteredFeature, KoinComponent {
    private val featureRegistry: FeatureRegistry by inject()

    override val featureInfo: FeatureInfo = getBotFeatureInfoUseCase.invoke()
    override val defaultCommand = null
    override val otherCommands = listOf(
        SupportedCommand(
            command = Command.TIP,
            description = "Dono arigato!",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.REPO,
            description = "Project repository",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.INVITE,
            description = "Bot invite link",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.DONATE,
            description = "Dono arigato!",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.HELP,
            description = "RTFM",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.COMMANDS,
            description = "Available commands",
            arguments = listOf(),
        ),
    )

    override suspend fun start() {
        Napier.d(tag = TAG) { "FightingNerd: ${featureInfo.version}" }
    }

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.TIP,
            Command.DONATE,
                -> createTipEmbed()

            Command.REPO -> createRepoText()
            Command.INVITE -> createInviteText()
            Command.HELP -> createHelpEmbed()
            Command.COMMANDS -> createCommandsEmbed()
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private fun createTipEmbed(): Result<BotOutput, BotError> {
        val embedBuilder: EmbedBuilder.() -> Unit = {
            title = "Dono arigato!"
            url = URL_KOFI
            color = Color(PURPLE)

            mandatoryField(
                name = "💸💸💸",
                value = "I don't drink coffee but feel free to support the server costs!\n" +
                        URL_KOFI
            )

            featureFooter(featureInfo)
        }

        return Result.Success(BotOutput(embedBuilder))
    }

    private fun createHelpEmbed(): Result<BotOutput, BotError> {
        val features = featureRegistry.getRegisteredFeatures()

        val embedBuilder: EmbedBuilder.() -> Unit = {
            title = "FightingNerd bot by @phd_cunnilingus"

            mandatoryField(
                name = "⚙️ Commands".uppercase(),
                value = "1. **tag** → `@FightingNerdBot` `[command]` `[query]`\n" +
                        "   - frame data (`fd`) is the default command; `@FightingNerdBot jun df1` works\n" +
                        "2. **slash** → `/command`\n\n" +
                        "Use **`/commands`** to see available commands\n\n"
            )

            mandatoryField(
                name = "🧩 FEATURE MODULES",
                value = features.joinToString("\n") { feature ->
                    val info = feature.featureInfo
                    val name = "- **[${info.name}](${info.url})** (${info.version})"
                    if (info.supportedGameSet.isEmpty()) {
                        name
                    } else {
                        val games = info.supportedGameSet.joinToString("\n") { game ->
                            "  - ${game.name}"
                        }
                        "$name:\n$games"
                    }
                },
                inline = false,
            )

            mandatoryField(
                name = "🫶 OTHER LINKS",
                value = buildString {
                    appendLine("- **[DONATE]($URL_KOFI)**")
                    appendLine("- **[INVITE]($URL_INVITE)**")
                    appendLine("- **[Repo]($URL_REPO)**")
                }
            )

            featureFooter(featureInfo)
        }

        val result = BotOutput(
            primaryEmbedBuilder = embedBuilder,
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Commands",
                        action = BotOutput.EmbedButton.Action.Query(Command.COMMANDS.name),
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_LONG_S.seconds,
            ),
        )

        return Result.Success(result)
    }

    private fun createCommandsEmbed(): Result<BotOutput, BotError> {
        val commands = Command.entries.sortedBy { it.name }
        val fdCommands = commands.filter { it.name.startsWith("FD") && it.name != "FD" }
        val charCommands = commands.filter { it.name.startsWith("CHAR") }
        val aliasCommands = commands.filter { it.name.startsWith("ALIAS") }
        val otherCommands = commands - fdCommands.toSet() - charCommands.toSet() - aliasCommands.toSet() - Command.FD - adminCommands.map { it.command }.toSet()

        val embedBuilder: EmbedBuilder.() -> Unit = {
            title = "⚙️ COMMANDS"

            mandatoryField(
                name = "📊 FRAME DATA",
                value = buildString {
                    append("- `${Command.FD.name}` (global)")
                    fdCommands
                        .sortedBy { it.name }
                        .forEach { fdCommand ->
                            append("\n  - `${fdCommand.name}`")
                        }
                    append("\n")
                }.trimEnd(),
            )

            mandatoryField(
                name = "🎭 CHARACTER DATA",
                value = buildString {
                    charCommands
                        .sortedBy { it.name }
                        .forEach { charCommand ->
                            append("- `${charCommand.name}`\n")
                        }
                }.trimEnd(),
            )

            mandatoryField(
                name = "🥸 CHARACTER ALIASES",
                value = buildString {
                    aliasCommands
                        .sortedBy { it.name }
                        .forEach { aliasCommand ->
                            append("- `${aliasCommand.name}`\n")
                        }
                }
            )

            mandatoryField(
                name = "🛠️ OTHER COMMANDS",
                value = buildString {
                    otherCommands.forEach { command ->
                        append("- `${command.name}`\n")
                    }
                }.trimEnd(),
            )

            featureFooter(featureInfo)
        }

        return Result.Success(BotOutput(primaryEmbedBuilder = embedBuilder))
    }

    private fun createRepoText(): Result<BotOutput, BotError> {
        return Result.Success(
            BotOutput(
                plainText = "Contribute to FightingNerd: $URL_REPO"
            )
        )
    }

    private fun createInviteText(): Result<BotOutput, BotError> {
        return Result.Success(
            BotOutput(
                plainText = "FightingNerd bot invite: $URL_INVITE"
            )
        )
    }

    private companion object {
        const val TAG = "CoreDiscordFeature"
        const val PURPLE = 0x00A020F0
    }
}