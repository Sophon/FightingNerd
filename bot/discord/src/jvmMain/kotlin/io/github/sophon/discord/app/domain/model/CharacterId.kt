package io.github.sophon.discord.app.domain.model

import io.github.sophon.core.featureConfig.model.Game

/**
 * Exact identity of a character - [characterId] is unique within [game].
 */
data class CharacterId(
    val game: Game,
    val characterId: String,
)
