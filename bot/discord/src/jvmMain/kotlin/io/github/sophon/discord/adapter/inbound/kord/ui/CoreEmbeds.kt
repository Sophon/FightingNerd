package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.URL_APP_STORE
import io.github.sophon.discord.URL_BUY_ME_COFFEE
import io.github.sophon.discord.URL_IMG_FIGHTING_NERD
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_PLAY_STORE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.model.response.SteamLobbyResponse
import io.github.sophon.discord.app.model.Command
import io.github.sophon.discord.app.model.adminCommands
import io.github.sophon.discord.feat.core.domain.CommandRegistry

internal fun errorEmbed(
    error: BotError,
    commandRegistry: CommandRegistry,
): EmbedBuilder.() -> Unit = {
    title = "ERROR"
    color = Color(RED)
    description = "**$error**".truncate(EMBED_MAX_LENGTH)

    mandatoryField(
        name = "↓↓↓ **CLICK THESE** ↓↓↓",
        value = "Slash commands have **auto complete**.",
        inline = false,
    )

    mandatoryField(
        name = "Frame Data",
        value = commandRegistry.mention(Command.Fd),
    )

    mandatoryField(
        name = "Character Names",
        value = commandRegistry.mention(Command.Alias),
    )

    mandatoryField(
        name = "Other",
        value = "${commandRegistry.mention(Command.Help)} | ${commandRegistry.mention(Command.Commands)}",
        inline = false,
    )

    footer {
        text = "Got something to say, nerd? Use `/feedback`"
        icon = URL_IMG_FIGHTING_NERD
    }
}

internal fun tipEmbed(
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "Dono arigato!"
    url = URL_KOFI
    color = Color(PURPLE)

    mandatoryField(
        name = "☕️ ☕️ ☕️",
        value = "I don't drink coffee but feel free to support the server costs!\n" +
                "- ${URL_KOFI}\n" +
                "- ${URL_BUY_ME_COFFEE}\n"
    )

    featureFooter(featureInfo)
}

internal fun modulesEmbed(
    modulesResponse: ModulesResponse,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "FightingNerd bot by @phd_cunnilingus"
    color = Color(PURPLE)

    val moduleList = modulesResponse.moduleList
    val chunks: List<List<ModulesResponse.Module>> = when (moduleList.size) {
        in 1..5 -> {
            listOf(moduleList)
        }
        in 5..EMBED_LIST_PER_COLUMN -> {
            moduleList.chunked(5)
        } else ->
            moduleList.chunked(EMBED_LIST_PER_COLUMN)
    }

    chunks.forEachIndexed { index, moduleChunk ->
        mandatoryField(
            name = if (index == 0) "🧩 FEATURE MODULES" else "_",
            value = moduleChunk.joinToString("\n") { module ->
                val games = module.gameList.joinToString("\n") { game ->
                    "  - $game"
                }
                "- **[${module.name}](${module.url})**:\n$games"
            },
        )
    }

    mandatoryField(
        name = "🫶 OTHER LINKS",
        value = buildString {
            appendLine("- **[DONATE]($URL_KOFI)**")
            appendLine("- **[INVITE]($URL_INVITE)**")
            appendLine("- **[Repo]($URL_REPO)**")
        },
        inline = false,
    )

    featureFooter(featureInfo)
}

internal fun commandsEmbed(
    commandList: List<Command>,
    commandRegistry: CommandRegistry,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit {
    val fdCommands = commandList.filter {
        it.name.startsWith("Fd")
                && it.name != "Fd"
    }
    val charCommands = commandList.filter {
        it.name.startsWith("Char")
    }
    val aliasCommands = commandList.filter {
        it.name.startsWith("Alias")
    }
    val invCommands = commandList.filter {
        it.name.startsWith("Inv")
                && it.name != "Invite"
    }
    val gameSpecificCommands = listOf(
        Command.Heat,
        Command.Homing,
        Command.Pc,
        Command.Stance,
        Command.ThrowTK,
        Command.Strings,
        Command.SpecialROA,
    )
    val rangeCommands = listOf(
        Command.Startup,
        Command.OnBlock,
        Command.OnHit,
        Command.OnCounter,
    )
    val excludedFromOthers = buildSet {
        addAll(fdCommands)
        addAll(charCommands)
        addAll(aliasCommands)
        addAll(invCommands)
        addAll(gameSpecificCommands)
        addAll(rangeCommands)
        add(Command.Fd)
        addAll(adminCommands)
    }
    val otherCommands = commandList.filterNot { it in excludedFromOthers }

    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "⚙️ COMMANDS"
        description = "Try clicking on the commands."
        color = Color(PURPLE)

        mandatoryField(
            name = "Frame Data",
            value = commandRegistry.mention(Command.Fd),
        )

        mandatoryField(
            name = "Character Data",
            value = commandRegistry.mention(Command.Char),
        )

        mandatoryField(
            name = "Character Names",
            value = "${commandRegistry.mention(Command.Alias)}: *${Command.Alias.description}*",
            inline = false,
        )

        mandatoryField(
            name = "Game Specific",
            value = buildString {
                gameSpecificCommands
                    .sortedBy { it.name }
                    .forEach { command ->
                        append("- ${commandRegistry.mention(command)}: *${command.description}*\n")
                    }
            }
        )

        mandatoryField(
            name = "Range: moves within range",
            value = buildString {
                rangeCommands
                    .sortedBy { it.name }
                    .forEach { command ->
                        append("- ${commandRegistry.mention(command)}\n")
                    }
            }
        )

        mandatoryField(
            name = "Other",
            value = buildString {
                otherCommands.forEach { command ->
                    append("- ${commandRegistry.mention(command)}: *${command.description}*\n")
                }
            }.trimEnd(),
        )

        featureFooter(featureInfo)
    }

    return embedBuilder
}

internal fun helpEmbed(
    commandRegistry: CommandRegistry,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "EXAMPLES"
    color = Color(PURPLE)
    description = "SLASH has **auto-complete**, TAG is faster."

    mandatoryField(
        name = "1. **SLASH**: `/command [optional queries]`",
        value = "- ${commandRegistry.mention(Command.Fd)} - *frame data*:\n" +
                "   - `/fd character:Law move:df1`\n" +
                "- ${commandRegistry.mention(Command.Heat)} | ${commandRegistry.mention(Command.Pc)} | ${commandRegistry.mention(Command.Homing)}:\n" +
                "   - `/heat character:nina`\n" +
                "   - `/pc character:leroy`\n" +
                "   - `/homing character:king`\n" +
                "- ${commandRegistry.mention(Command.Strings)}:\n" +
                "   - `/strings character:jin move:12`\n" +
                "- ${commandRegistry.mention(Command.Stance)}:\n" +
                "   - `/stance character:jin`\n" +
                "   - `/stance character:jin stance:zen`\n" +
                "- ${commandRegistry.mention(Command.Startup)} | ${commandRegistry.mention(Command.OnBlock)} | " +
                "${commandRegistry.mention(Command.OnHit)} | ${commandRegistry.mention(Command.OnCounter)}:\n" +
                "   - `/startup character:ak value:13`\n" +
                "   - `/onBlock character:alisa value:-10 bound:-inf`\n" +
                "   - `/onHit character:lee value:5 bound:inf`\n",
        inline = false,
    )

    mandatoryField(
        name = "2. **TAGGING**: `@bot [command] [optional queries]`",
        value = "- **`fd`** is the default command, no need to type it.\n" +
                "- **`fd`** syntax: `[charName] [moveInput]`\n" +
                "   - `@bot hisui 5b` - no command, defaults to **`fd`**\n" +
                "   - `@bot ak h.db21` - no command, defaults to **`fd`**\n" +
                "   - `@bot fd sol 236h` - identical without **`fd`**\n" +
                "   - `@bot char baiken` - **`char`** command\n" +
                "- same commands as with slash",
    )

    mandatoryField(
        name = "QUERIES",
        value = "- character names must be a __**single word without spaces**__\n" +
                "- all queries are separated by a single space\n" +
                "   - **wrong command?** Try ${commandRegistry.mention(Command.Help)} or ${commandRegistry.mention(Command.Commands)}\n" +
                "   - **wrong name?** Try ${commandRegistry.mention(Command.Alias)}\n" +
                "   - **wrong move?** western notation or numpad notation\n" +
                "      - for Tekken, consider ${commandRegistry.mention(Command.Stance)}, ${commandRegistry.mention(Command.Strings)}, ${commandRegistry.mention(Command.Pc)} or ${commandRegistry.mention(Command.Heat)}\n" +
                "      - check the Wiki to see the proper notation\n" +
                "- some outputs have buttons, clicking those outputs the proper query",
    )

    featureFooter(featureInfo)
}

internal fun steamLobbyEmbed(
    steamLobby: SteamLobbyResponse,
): EmbedBuilder.() -> Unit = {
    title = "Join ${steamLobby.hostName}'s lobby!"
    color = Color(PURPLE)

    optionalField(
        name = "Lobby name",
        value = steamLobby.lobbyName?.let { "```$it```" },
        inline = false,
    )

    optionalField(
        name = "Password",
        value = steamLobby.password?.let { "```$it```" },
        inline = false,
    )
}

internal fun promoEmbed(): EmbedBuilder.() -> Unit = {
    title = "ENJOY THE BOT?"
    color = Color(PURPLE)

    mandatoryField(
        name = "",
        value = "Buy me a coffee.\n" +
                "The project is also available on mobile.",
    )
}

internal fun promoButtonSet(): BotResponse.ButtonSet = BotResponse.ButtonSet(
    buttonList = listOf(
        BotResponse.EmbedButton(
            label = "☕️ KO-FI",
            action = BotResponse.EmbedButton.Action.Url(URL_KOFI),
        ),
        BotResponse.EmbedButton(
            label = "☕️ BUY-ME-COFFEE",
            action = BotResponse.EmbedButton.Action.Url(URL_BUY_ME_COFFEE),
        ),
        BotResponse.EmbedButton(
            label = "🍏 iPhone",
            action = BotResponse.EmbedButton.Action.Url(URL_APP_STORE),
        ),
        BotResponse.EmbedButton(
            label = "🤖 Android",
            action = BotResponse.EmbedButton.Action.Url(URL_PLAY_STORE),
        ),
    ),
)


private const val PURPLE = 0x00A020F0
private const val RED = 0x00FF0000
