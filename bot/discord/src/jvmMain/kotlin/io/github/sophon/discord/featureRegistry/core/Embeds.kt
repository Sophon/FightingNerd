package io.github.sophon.discord.featureRegistry.core

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField

internal fun moveListEmbed(
    category: String,
    dataList: List<String>,
    featureInfo: FeatureInfo,
    color: Color,
): EmbedBuilder.() -> Unit = {
    this.color = color

    //don't join, chunk, then join
    val numberedMoves = dataList
        .mapIndexed { index, data ->
            "${index + 1}. **${data}**"
        }

    numberedMoves
        .chunked(EMBED_LIST_PER_COLUMN)
        .forEachIndexed { index, moveList ->
            val text = moveList.joinToString("\n")
            val name = if (index == 0) "$category moves" else "_"
            mandatoryField(
                name = name,
                value = text,
            )
        }

    featureFooter(featureInfo)
}