package io.github.sophon.discord.util

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.truncate
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.URL_BUY_ME_COFFEE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_TOPGG
import io.github.sophon.discord.feat.core.domain.model.BotOutput

internal fun EmbedBuilder.mandatoryField(
    name: String,
    value: String?,
    inline: Boolean = true,
    escapeAsterisks: Boolean = false,
) {
    val formatted = value
        .orDash()
        .let { value ->
            if (escapeAsterisks) value.replace("*", "\\*")
            else value
        }
        .truncate(EMBED_MAX_LENGTH)

    field {
        this.name = name
        this.value = formatted
        this.inline = inline
    }
}

internal fun EmbedBuilder.optionalField(
    name: String,
    value: String?,
    inline: Boolean = true,
    escapeAsterisks: Boolean = false,
) {
    val formatted = value
        .orDash()
        .let { value ->
            if (escapeAsterisks) value.replace("*", "\\*")
            else value
        }
        .truncate(EMBED_MAX_LENGTH)

    if (value.isNullOrBlank().not()) {
        field {
            this.name = name
            this.value = formatted
            this.inline = inline
        }
    }
}

internal fun EmbedBuilder.optionalField(
    name: String,
    delimiter: String = "|",
    inline: Boolean = true,
    values: List<String?>,
) {
    if (values.all { it.isNullOrBlank() }) return

    val joinedValues = values
        .joinToString(" $delimiter ") { value ->
            value.takeUnless { it.isNullOrBlank() }.orDash()
        }

    field {
        this.name = name
        this.value = joinedValues.truncate(EMBED_MAX_LENGTH)
        this.inline = inline
    }
}

internal fun EmbedBuilder.separator() {
    field {
        name = "\u200B"
        value = ""
        inline = false
    }
}

internal fun EmbedBuilder.featureFooter(featureInfo: FeatureInfo) {
    footer {
        text = "${featureInfo.name}\n" +
                "Ideas or errors? Use /feedback"
        icon = featureInfo.iconUrl
    }
}

internal fun List<Move>.toButtons(charName: String): List<BotOutput.EmbedButton> {
    return mapIndexed { index, move ->
        val query = "$charName ${move.input}"
        BotOutput.EmbedButton(
            label = (index + 1).toString(),
            action = BotOutput.EmbedButton.Action.Query(query),
        )
    }
}

internal fun hitboxImages(
    urls: Move.Urls
): EmbedBuilder.() -> Unit = {
    val images = urls.hitboxImageList.takeIf { it.isNotEmpty() }
        ?: emptyList()
    images
        .takeIf { it.size == 1 }
        ?.let { image = it.first() }
}

internal fun donationMessage(): String {
    return "Enjoy the bot? Buy me a coffee:\n" +
            "- ☕️ <$URL_KOFI>\n" +
            "- ☕️ <$URL_BUY_ME_COFFEE>\n" +
            "- ⭐️ rate me at: <$URL_TOPGG>"
}
