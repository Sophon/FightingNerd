package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.util.invisibleChar
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.URL_BUY_ME_COFFEE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.app.model.response.BotResponse

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
        name = invisibleChar
        value = ""
        inline = false
    }
}

internal fun EmbedBuilder.featureFooter(dataSource: BotResponse.DataSource) {
    footer {
        text = "${dataSource.name}\n" +
                "Ideas or errors? Use /feedback"
        icon = dataSource.iconUrl
    }
}

/**
 * Due to how Discord embedding works:
 * 1. one image - part of embed
 * 2. more images - separate post but with the same URL in the title ()
 *
 * Hence, this function should be no-op if there are multiple images.
 */
internal fun EmbedBuilder.embedImage(urls: List<String>) {
    val images = urls.takeIf { it.isNotEmpty() }
        ?: emptyList()
    images
        .takeIf { it.size == 1 }
        ?.let { image = it.first() }
}

internal fun EmbedBuilder.detailsBulletPoints(fields: List<BotResponse.Field>) {
    if (fields.isEmpty()) return

    separator()

    val columnSize = ((fields.size + DETAILS_COLUMN_COUNT - 1) / DETAILS_COLUMN_COUNT)

    fields
        .chunked(columnSize)
        .forEach { column ->
            val lines = column.joinToString(separator = "\n") { field ->
                "- **${field.title}**: ${field.value}"
            }

            field {
                this.name = invisibleChar
                this.value = lines.truncate(EMBED_MAX_LENGTH)
                this.inline = true
            }
        }
}

internal fun donationMessage(): String {
    return "Enjoy the bot? Buy me a coffee:\n" +
            "- ☕️ <$URL_KOFI>\n" +
            "- ☕️ <$URL_BUY_ME_COFFEE>\n" +
            "\nAlso available on phones:\n" +
            "- 🤖 <https://play.google.com/store/apps/details?id=io.github.sophon.fightingnerd>\n" +
            "- 🍏 <https://apps.apple.com/us/app/fighting-nerd/id6793185357>"
}


private const val DETAILS_COLUMN_COUNT = 3

