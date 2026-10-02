package io.github.sophon.discord.adapter.inbound.kord

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.discord.app.domain.model.ButtonEvent
import io.github.sophon.discord.app.domain.model.MoveId
import io.github.sophon.discord.feat.core.domain.model.DiscordButton
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Null for a button the new API doesn't handle - the legacy Edit and Redirect buttons,
 * and legacy Query buttons, whose plain query doesn't decode into a [MoveId].
 */
internal fun decodeToButtonEvent(buttonId: String): ButtonEvent? {
    val (key, value) = buttonId
        .split(DiscordButton.BUTTON_ID_DELIMITER, limit = 2)
        .takeIf { it.size == 2 }
        ?: return null

    val buttonEvent = when (key) {
        DiscordButton.KEY_EXPAND -> decodeMoveId(value)?.let { moveId -> ButtonEvent.Expand(moveId) }
        DiscordButton.KEY_QUERY -> decodeMoveId(value)?.let { moveId -> ButtonEvent.Query(moveId) }
        DiscordButton.KEY_TEXT -> ButtonEvent.Text(value)
        else -> null
    }
    return buttonEvent
}

private fun decodeMoveId(value: String): MoveId? {
    val (gameName, characterId, input) = value
        .split(DiscordButton.BUTTON_ID_DELIMITER, limit = 3)
        .takeIf { it.size == 3 }
        ?: return null
    val game = Game.entries.firstOrNull { it.name == gameName } ?: return null

    val moveId = MoveId(game = game, characterId = characterId, input = input)
    return moveId
}
