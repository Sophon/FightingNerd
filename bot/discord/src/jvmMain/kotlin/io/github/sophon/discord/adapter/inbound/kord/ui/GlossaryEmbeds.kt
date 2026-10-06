package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.app.model.response.GlossaryResponse

internal fun glossaryEmbed(
    glossary: GlossaryResponse,
): EmbedBuilder.() -> Unit = {
    title = glossary.term
    url = glossary.termUrl
    color = Color(glossary.dataSource.color)

    glossary.imageUrl?.let { image = it }

    glossary.definition
        .chunkByNewLines(delimiter = ".", maxLength = EMBED_MAX_LENGTH)
        .forEach { chunk ->
            mandatoryField(
                name = "",
                value = chunk,
                inline = false,
            )
        }

    val jpTranslationString = glossary.jpTranslationList
        .joinToString(separator = "") { "* $it\n" }
    mandatoryField(name = "🇯🇵", value = jpTranslationString, inline = false)

    glossary.videoUrl?.let { videoUrl ->
        mandatoryField(name = "Video", value = "[Link]($videoUrl)")
    }

    featureFooter(glossary.dataSource)
}
