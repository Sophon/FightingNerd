package io.github.sophon.discord.app.model.frameData

import io.github.sophon.wiki.model.wiki.Game

/**
 * Exact identity of a move - [characterId] is unique within [game], [input] is unique within the character.
 */
data class MoveId(
    val game: Game,
    val characterId: String,
    val input: String,
)
