package io.github.sophon.discord.feat.core.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.EMBED_LIST_MIN_COLUMN
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
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

        val chunks = when (numberedMoves.size) {
            in 0..EMBED_LIST_MIN_COLUMN -> numberedMoves.size
            in EMBED_LIST_MIN_COLUMN..EMBED_LIST_PER_COLUMN -> numberedMoves.size / 2
            else -> EMBED_LIST_PER_COLUMN
        }

        numberedMoves
            .chunked(chunks)
            .forEachIndexed { index, moveList ->
                val text = moveList.joinToString("\n")
                val name = if (index == 0) "$formattedTitle moves" else "_"
                mandatoryField(
                    name = name,
                    value = text,
                )
            }
    }

    featureFooter(featureInfo)
}