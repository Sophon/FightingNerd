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

internal fun examplesEmbed(
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "EXAMPLES"
    color = Color(PURPLE)

    mandatoryField(
        name = "INPUT METHODS",
        value = "1. **SLASH** (has AUTOCOMPLETE): `/command [optional queries]`\n" +
                "   - **FD** (frame data):\n" +
                "      - `/alias game: Tekken_8`\n" +
                "   - **Heat**, **PC**, **Homing**:\n" +
                "      - `/heat character:nina`\n" +
                "      - `/pc character:leroy`\n" +
                "      - `/homing character:king`\n" +
                "   - **Strings**:\n" +
                "      - `/strings character:kazuya move:12`\n" +
                "   - **Stance**:\n" +
                "      - `/strings character:lidia\n" +
                "      - `/strings character:lidia stance:hae`\n\n" +
                "2. **TAGGING** (is faster): `@bot [command] [optional queries]`\n" +
                "   - **`fd`** is the default command, no need to type it.\n" +
                "   - **`fd`** syntax: `[charName] [moveInput]`\n" +
                "      - `@bot hisui 5b` - no command, defaults to **`fd`**\n" +
                "      - `@bot ak h.db21` - no command, defaults to **`fd`**\n" +
                "      - `@bot fd sol 236h` - identical without **`fd`**" +
                "      - `@bot char baiken` - **`char`** command",
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

internal fun helpEmbed(
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "HOW TO USE THE BOT"
    color = Color(PURPLE)

    mandatoryField(
        name = "**Basic syntax**",
        value = "`@bot [command] [queries]` or `/command [queries]`\n" +
                "  - tag is quicker, **slash has autocomplete**\n" +
                "  - don't know the char's name? Use `alias`\n",
        inline = false,
    )

    val bulletPoints = listOf(
        "1. **frame data** - `fd` default command, no need to write `fd` when tagging\n" +
                "  - `@bot jin df1` or `/fd feng bt.1`  or `@bot ak h.bad.32`",
        "2. **list of moves** - `pc`, `homing`, `heat`, `throwtk` (Tekken), `inv`\n" +
                "  - `@bot homing steve` or `/homing hwo` or `@bot invgg sol`\n" +
                "  - pressing a button shows the frame data of the corresponding move",
        "3. **stances** (Tekken) - `stance`\n" +
                "  - has two variants, `stance char` and `stance char specificStance`\n" +
                "  - `@bot stance ling` or `/stance lidia` - pressing a button shows all moves of that stance\n" +
                "  - `@bot stance ak bad` or `/stance bob bal` - pressing a button shows frame data of the corresponding move",
        "4. **followups** (Tekken) - `strings` \n" +
                "  - shows all the followups of a move\n" +
                "  - `@bot strings kaz 1` or `/bot strings miary df1`"
    )

    val chunks = when (bulletPoints.size) {
        in 0..5 -> listOf(bulletPoints)
        in 6..EMBED_LIST_PER_COLUMN -> bulletPoints.chunked(5)
        else -> bulletPoints.chunked(EMBED_LIST_PER_COLUMN)
    }

    chunks.forEach { bulletPoints ->
        mandatoryField(
            name = "",
            value = bulletPoints.joinToString("\n")
        )
    }

    mandatoryField(
        name = "",
        value = "Send feedback to author: `feedback`.\n" +
                "Supported games and features: `modules`.",
        inline = false,
    )

    featureFooter(featureInfo)
}

internal fun aliasEmbed(
    commandList: List<Command>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    mandatoryField(
        name = "🥸 ALIAS",
        value = buildString {
            commandList.forEach { fdCommand ->
                append("- `${fdCommand.name}`\n")
            }
        }.trimEnd(),
    )

    featureFooter(featureInfo)
}


private const val PURPLE = 0x00A020F0