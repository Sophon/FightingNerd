package io.github.sophon.discord.domain

internal sealed class DiscordButton(
    private val key: String,
    private val value: String,
) {
    class Query(val query: String): DiscordButton(key = KEY_QUERY, value = query)

    class Edit(val messageId: String): DiscordButton(key = KEY_EDIT, value = messageId)

    class Redirect(val channelId: String): DiscordButton(key = KEY_REDIRECT, value = channelId)


    override fun toString(): String {
        return "$key:$value"
    }


    internal companion object {
        const val KEY_QUERY = "query"
        const val KEY_EDIT = "edit"
        const val KEY_REDIRECT = "redirect"
    }
}
