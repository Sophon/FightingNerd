package io.github.sophon.discord.feat.infilGlossary

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.glossaryinfil.integration.model.GlossaryItem

internal fun glossaryEmbed(
    item: GlossaryItem,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = item.term
    url = item.url.term
    color = Color(BROWN)

    item.url.image?.let { image = it }

    val embedData = item.definition.chunkByNewLines(delimiter = ".", maxLength = EMBED_MAX_LENGTH)
    embedData.forEach { data ->
        mandatoryField(
            name = "",
            value = data,
            inline = false
        )
    }

    val japaneseValueString = item.jpTranslation
        .joinToString(separator = "") { "* $it\n" }
    mandatoryField(name = "🇯🇵", value = japaneseValueString, inline = false)

    item.url.video?.let { url ->
        mandatoryField(name = "Video", value = "[Link]($url)")
    }

    featureFooter(featureInfo)
}

private const val BROWN = 0xDAA06D