package io.github.sophon.discord.feat.core.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.toColumns
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.adapter.inbound.kord.ui.featureFooter
import io.github.sophon.discord.adapter.inbound.kord.ui.mandatoryField
import io.github.sophon.discord.app.model.Emoji
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.model.MoveRange

internal fun moveListEmbed(
    category: String,
    dataList: List<String>,
    featureInfo: FeatureInfo,
    color: Color,
    emoji: Emoji? = null,
): EmbedBuilder.() -> Unit = {
    val formattedTitle = emoji?.let { "$it $category" } ?: category

    this.color = color

    if (dataList.isEmpty()) {
        mandatoryField(
            name = "$formattedTitle moves",
            value = "Nothing found 😔"
        )
    } else {
        val numberedMoves = dataList
            .mapIndexed { index, data ->
                "${index + 1}. **${data}**"
            }

        mandatoryField(
            name = "$formattedTitle moves",
            value = "",
            inline = false,
        )

        numberedMoves
            .toColumns()
            .forEach { moveList ->
                val text = moveList.joinToString("\n")
                mandatoryField(
                    name = "",
                    value = text,
                )
            }
    }

    featureFooter(featureInfo)
}

internal fun moveListEmbed(
    moveRange: MoveRange,
    featureInfo: FeatureInfo,
    color: Color,
    customFormatter: (Move) -> String? = { it.input },
): EmbedBuilder.() -> Unit = {
    this.color = color
    val formattedTitle = "${moveRange.character.displayName} ${moveRange.rangeType.name} " +
            "[${moveRange.formattedMin} ; ${moveRange.formattedMax}]"

    if (moveRange.moveList.isEmpty()) {
        mandatoryField(
            name = "$formattedTitle moves",
            value = "Nothing found 😔"
        )
    } else {
        val numberedMoves = moveRange.moveList
            .mapIndexed { index, move ->
                "${index + 1}. **${customFormatter(move)}**"
            }

        mandatoryField(
            name = "$formattedTitle moves",
            value = "",
            inline = false,
        )

        numberedMoves
            .toColumns()
            .forEach { moveList ->
                val text = moveList.joinToString("\n")
                mandatoryField(
                    name = "",
                    value = text,
                )
            }
    }

    featureFooter(featureInfo)
}

internal fun aliasEmbed(
    characterList: List<Character>,
    featureInfo: FeatureInfo,
    colorCode: Int,
): EmbedBuilder.() -> Unit = {
    color = Color(colorCode)

    val aliasList = characterList
        .filter { it.aliasList.isNotEmpty() }
        .sortedBy { it.displayName }
        .mapIndexed { index, character ->
            val aliases = character.aliasList.joinToString(", ")
            "${index + 1}. **${character.displayName}** → $aliases"
        }

    mandatoryField(
        name = "🥸 Character aliases",
        value = "",
        inline = false,
    )

    aliasList
        .toColumns()
        .forEach { aliasList ->
            val text = aliasList.joinToString("\n")
            mandatoryField(
                name = "",
                value = text,
            )
        }

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

private const val PURPLE = 0x00A020F0