package io.github.sophon.discord.feat.core.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.toColumns
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.discord.feat.core.domain.model.Emoji
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField

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
            .forEachIndexed { _, moveList ->
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
