package io.github.sophon.discord.app.domain.model

import io.github.sophon.core.featureConfig.model.Game

/**
 * Exact identity of a move - [characterId] is unique within [game], [input] is unique within the character.
 */
data class MoveId(
    val game: Game,
    val characterId: String,
    val input: String,
)
