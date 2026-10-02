package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.MoveId

internal sealed class DiscordButton(
    private val key: String,
    private val value: String,
) {
    class Query(val query: String): DiscordButton(key = KEY_QUERY, value = query) {
        constructor(moveId: MoveId): this(query = moveId.toButtonValue())
    }

    class Edit(val messageId: String): DiscordButton(key = KEY_EDIT, value = messageId)

    class Redirect(val channelId: String): DiscordButton(key = KEY_REDIRECT, value = channelId)

    class Text(val text: String): DiscordButton(key = KEY_TEXT, value = text)

    class Expand(val moveId: MoveId): DiscordButton(key = KEY_EXPAND, value = moveId.toButtonValue())

    class Command(
        command: io.github.sophon.discord.app.domain.model.Command,
        query: String,
    ): DiscordButton(key = KEY_COMMAND, value = command.toButtonValue(query))


    override fun toString(): String {
        return "$key$BUTTON_ID_DELIMITER$value"
    }


    internal companion object {
        const val KEY_QUERY = "query"
        const val KEY_EDIT = "edit"
        const val KEY_REDIRECT = "redirect"
        const val KEY_TEXT = "text"
        const val KEY_EXPAND = "expand"
        const val KEY_COMMAND = "command"

        const val BUTTON_ID_DELIMITER = ":"
    }
}


/**
 * `input` goes last - it may contain the delimiter, so decoding splits with a limit.
 */
private fun MoveId.toButtonValue(): String {
    val buttonValue = listOf(game.name, characterId, input).joinToString(DiscordButton.BUTTON_ID_DELIMITER)

    return buttonValue
}

/**
 * `query` goes last - it may contain the delimiter, so decoding splits with a limit.
 */
private fun Command.toButtonValue(query: String): String {
    val buttonValue = listOf(name, query).joinToString(DiscordButton.BUTTON_ID_DELIMITER)

    return buttonValue
}
