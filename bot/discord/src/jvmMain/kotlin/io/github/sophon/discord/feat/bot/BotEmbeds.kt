package io.github.sophon.discord.feat.bot

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
import io.github.sophon.discord.URL_BUY_ME_COFFEE
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.feat.admin.adminCommands
import io.github.sophon.discord.feat.core.domain.CommandRegistry
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField

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
    featureList: List<DiscordRegisteredFeature>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "FightingNerd bot by @phd_cunnilingus"
    color = Color(PURPLE)

    val chunks: List<List<DiscordRegisteredFeature>> = when (featureList.size) {
        in 1..5 -> {
            listOf(featureList)
        }
        in 5..EMBED_LIST_PER_COLUMN -> {
            featureList.chunked(5)
        } else ->
            featureList.chunked(EMBED_LIST_PER_COLUMN)
    }

    chunks.forEachIndexed { index, featureList ->
        mandatoryField(
            name = if (index == 0) "🧩 FEATURE MODULES" else "_",
            value = featureList.joinToString("\n") { feature ->
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
    val excludedFromOthers = buildSet {
        addAll(fdCommands)
        addAll(charCommands)
        addAll(aliasCommands)
        addAll(invCommands)
        addAll(gameSpecificCommands)
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

    mandatoryField(
        name = "1. SLASH has **auto-complete**, TAG is faster.",
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
                "   - `/stance character:jin stance:zen`"
    )

    mandatoryField(
        name = "2. **TAGGING**: `@bot [command] [optional queries]`",
        value = "- **`fd`** is the default command, no need to type it.\n" +
                "- **`fd`** syntax: `[charName] [moveInput]`\n" +
                "   - `@bot hisui 5b` - no command, defaults to **`fd`**\n" +
                "   - `@bot ak h.db21` - no command, defaults to **`fd`**\n" +
                "   - `@bot fd sol 236h` - identical without **`fd`**" +
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
        inline = false,
    )

    featureFooter(featureInfo)
}


private const val PURPLE = 0x00A020F0