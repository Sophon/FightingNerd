package io.github.sophon.discord.app.model

import io.github.sophon.wiki.model.wiki.Game

/**
 * Exact identity of a character - [characterId] is unique within [game].
 */
data class CharacterId(
    val game: Game,
    val characterId: String,
)
