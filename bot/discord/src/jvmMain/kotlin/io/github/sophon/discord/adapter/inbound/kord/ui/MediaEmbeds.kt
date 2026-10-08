package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.discord.app.model.response.MediaResponse

internal fun imagesMediaEmbed(
    media: MediaResponse.ImagesMediaResponse,
): EmbedBuilder.() -> Unit = {
    color = media.dataSource.color?.let { Color(it) }
    title = media.input
    url = media.url
    description = "**${media.characterName}**"

    embedImage(urls = media.imageList)

    featureFooter(dataSource = media.dataSource)
}

/**
 * Plain text, so Discord embeds the video itself.
 */
internal fun videoMediaText(
    media: MediaResponse.VideoMediaResponse,
): String {
    val text = buildString {
        appendLine("**${media.characterName}**: `${media.input}`")
        append(media.videoUrl.orEmpty())
    }
    return text
}
