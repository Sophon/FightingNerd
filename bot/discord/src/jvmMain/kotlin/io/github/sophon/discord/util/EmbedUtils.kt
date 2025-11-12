package io.github.sophon.discord.util

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.util.orDash
import io.github.sophon.discord.BotError

internal fun EmbedBuilder.mandatoryField(
    name: String,
    value: String?,
    inline: Boolean = true,
) {
    field {
        this.name = name
        this.value = value.orDash()
        this.inline = inline
    }
}

internal fun EmbedBuilder.optionalField(
    name: String,
    value: String?,
    inline: Boolean = true
) {
    if (value.isNullOrBlank().not()) {
        field {
            this.name = name
            this.value = value
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
        this.value = joinedValues
        this.inline = inline
    }
}

internal fun createErrorEmbed(error: BotError): EmbedBuilder.() -> Unit = {
    title = "Error"
    description = error.toString()
    color = Color(RED)
}

internal fun EmbedBuilder.separator() {
    field {
        name = "\u200B"
        value = ""
        inline = false
    }
}

private const val RED = 0xEE4B2B