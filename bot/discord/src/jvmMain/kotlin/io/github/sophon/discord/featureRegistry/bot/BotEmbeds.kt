package io.github.sophon.discord.featureRegistry.bot

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.admin.adminCommands
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField

internal fun tipEmbed(
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
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

internal fun modulesEmbed(
    featureList: List<DiscordRegisteredFeature>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "FightingNerd bot by @phd_cunnilingus"
    color = Color(PURPLE)

    featureFooter(featureInfo)


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
}

internal fun commandsEmbed(
    commandList: List<Command>,
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
                        append("- `${command.name}`\n")
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

    return embedBuilder
}

internal fun examplesEmbed(
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
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

    featureFooter(featureInfo)
}


private const val PURPLE = 0x00A020F0