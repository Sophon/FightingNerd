package io.github.sophon.botdiscord.util

import io.github.sophon.botdiscord.BotError
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder

internal fun EmbedBuilder.field(
    name: String,
    value: String?,
    inline: Boolean = true,
) {
    field {
        this.name = name
        this.value = value.orEmpty()
        this.inline = inline
    }
}

internal fun createErrorEmbed(error: BotError): EmbedBuilder.() -> Unit = {
    title = "Error"
    description = error.toString()
    color = Color(RED)
}

private const val RED = 0xEE4B2B