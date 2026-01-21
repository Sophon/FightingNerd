package io.github.sophon.discord.featureRegistry.bot

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.FeatureRegistry
import io.github.sophon.discord.featureRegistry.admin.adminCommands
import io.github.sophon.discord.featureRegistry.bot.usecase.CreateJoinEmbedButtonUseCase
import io.github.sophon.discord.featureRegistry.bot.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.Source
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

internal class BotFeature(
    getBotFeatureInfoUseCase: GetBotFeatureInfoUseCase,
    private val createJoinEmbedButtonUseCase: CreateJoinEmbedButtonUseCase,
): DiscordRegisteredFeature, KoinComponent {
    private val featureRegistry: FeatureRegistry by inject()

    override val featureInfo: FeatureInfo = getBotFeatureInfoUseCase.invoke()
    override val defaultCommand = null
    override val otherCommands = listOf(
        Command.Tip,
        Command.Repo,
        Command.Invite,
        Command.Donate,
        Command.Help,
        Command.Commands,
        Command.Examples,
        Command.Join,
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
            Command.Tip,
            Command.Donate,
                -> createTipEmbed()

            Command.Repo -> createRepoText()
            Command.Invite -> createInviteText()
            Command.Help -> createHelpEmbed()
            Command.Commands -> createCommandsEmbed()
            Command.Examples -> createExamples()
            Command.Join -> createJoinEmbedButtonUseCase.invoke(origin, query)

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
            color = Color(PURPLE)

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
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Commands.name
                        ),
                    ),
                    BotOutput.EmbedButton(
                        label = "Examples",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Examples.name
                        ),
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            ),
        )

        return Result.Success(result)
    }

    private fun createCommandsEmbed(): Result<BotOutput, BotError> {
        val commands = Command.entries.sortedBy { it.name }
        val fdCommands = commands.filter {
            it.name.startsWith("Fd")
                    && it.name != "Fd"
        }
        val charCommands = commands.filter {
            it.name.startsWith("Char")
        }
        val aliasCommands = commands.filter {
            it.name.startsWith("Alias")
        }
        val invCommands = commands.filter {
            it.name.startsWith("Inv")
                    && it.name != "Invite"
        }
        val gameSpecificCommands = listOf(
            Command.Heat,
            Command.Homing,
            Command.Pc,
            Command.Stance,
            Command.ThrowTK,
        )
        val excludedFromOthers = buildSet {
            addAll(fdCommands)
            addAll(charCommands)
            addAll(aliasCommands)
            addAll(invCommands)
            addAll(gameSpecificCommands)
            add(Command.Fd)
            addAll(adminCommands)
        }
        val otherCommands = commands.filterNot { it in excludedFromOthers }

        val embedBuilder: EmbedBuilder.() -> Unit = {
            title = "⚙️ COMMANDS"
            color = Color(PURPLE)

            mandatoryField(
                name = "📊 FRAME DATA",
                value = buildString {
                    append("- `${Command.Fd.name}` (global)")
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
                name = "🛡️ INVINCIBLE MOVES",
                value = buildString {
                    invCommands
                        .sortedBy { it.name }
                        .forEach { command ->
                            append("- `${command.name}`\n")
                        }
                }
            )

            mandatoryField(
                name = "🎮 GAME SPECIFIC",
                value = buildString {
                    gameSpecificCommands
                        .sortedBy { it.name }
                        .forEach { command ->
                            append("- `${command}`\n")
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

        val result = BotOutput(
            primaryEmbedBuilder = embedBuilder,
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Examples",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Examples.name
                        )
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        )

        return Result.Success(result)
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

    private fun createExamples(): Result<BotOutput, BotError> {
        val embedBuilder: EmbedBuilder.() -> Unit = {
            title = "EXAMPLES"
            color = Color(PURPLE)

            mandatoryField(
                name = "INPUT METHODS",
                value = "1. **TAGGING**: `@bot [command] [optional query] ...`\n" +
                        "   - **`fd`** is the default command, no need to type it. Only type the game specific **`fd`** like **`fdsf`** with crossover characters\n" +
                        "   - **`fd`** has the following syntax: `[charName] [moveInput]`\n" +
                        "   - Examples:\n" +
                        "      - `@bot hisui 5b` (no command, defaults to **`fd`**)\n" +
                        "      - `@bot ak h.db21` (no command, defaults to **`fd`**)\n" +
                        "      - `@bot fdcotw mai f.a` (game specific **`fd`**)\n" +
                        "      - `@bot chargg baiken`\n\n" +
                        "2. **SLASH**: `/command [optional query] ...`\n" +
                        "   - the amount of queries can vary from zero to many\n" +
                        "   - Examples:\n" +
                        "      - `/aliasmb`\n" +
                        "      - `/fd nina df12`\n" +
                        "      -  `/stance leroy hrm`\n" +
                        "      - past the command, the syntax is identical to tagging",
                inline = false,
            )

            mandatoryField(
                name = "QUERIES",
                value = "- each individual query must be a __**single word without spaces**__\n" +
                        "- all queries are separated by a single space\n" +
                        "   - **wrong command?** Try **`help`** or **`commands`**\n" +
                        "   - **wrong name?** Try game specific **`alias`** → **`aliasgg`** or **`aliastk`**\n" +
                        "   - **wrong move?** western notation or numpad notation\n" +
                        "      - for Tekken, consider **`stance`** or **`pc`** or **`heat`**\n" +
                        "      - check the Wiki to see the proper notation\n" +
                        "- some outputs have buttons, clicking those outputs the proper query"
            )
        }

        val result = BotOutput(
            primaryEmbedBuilder = embedBuilder,
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Commands",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Commands.name
                        ),
                    )
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        )

        return Result.Success(result)
    }

    private companion object Companion {
        const val TAG = "CoreDiscordFeature"
        const val PURPLE = 0x00A020F0
        const val KEY_JOIN = "url"
        const val KEY_PW = "password"
    }
}