package io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.MoveGameProperties

/**
 * One wiki's extension tables - one per game, 1:1 with a `character` / `move` row, keyed by that row's ID.
 * Called inside the adapter's transaction.
 */
internal interface SqlDelightGameProperties {
    fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    )

    fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    )

    /**
     * Keyed by the `character` row ID.
     */
    fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties>

    /**
     * Keyed by the `move` row ID.
     */
    fun loadMoveProperties(characterId: CharacterId): Map<Long, MoveGameProperties>
}