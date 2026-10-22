package io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties

import io.github.aakira.napier.Napier
import io.github.sophon.wiki.adapter.outbound.sqldelight.LazyWikiDB
import io.github.sophon.wiki.data.wavu.Tag2_move
import io.github.sophon.wiki.data.wavu.Tekken5_move
import io.github.sophon.wiki.data.wavu.Tekken8_move
import io.github.sophon.wiki.model.CharacterGameProperties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.MoveGameProperties
import io.github.sophon.wiki.model.game.T5Properties
import io.github.sophon.wiki.model.game.T8Properties
import io.github.sophon.wiki.model.game.Tag2Properties
import io.github.sophon.wiki.model.wiki.Game

internal class WavuSqlDelightGameProperties(
    wikiDatabase: LazyWikiDB,
) : SqlDelightGameProperties {
    private val database by wikiDatabase

    // Wavu characters have no game properties
    override fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    ) = Unit

    override fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    ) {
        when (properties) {
            is T8Properties -> database.tekken8MoveQueries.upsert(
                move_id = moveRowId,
                is_heat = properties.isHeat,
                is_homing = properties.isHoming,
                stance = properties.stance,
                is_power_crush = properties.isPowerCrush,
                is_high_crush = properties.isHighCrush,
                is_low_crush = properties.isLowCrush,
                has_wall_interaction = properties.hasWallInteraction,
                has_floor_interaction = properties.hasFloorInteraction,
            )

            is Tag2Properties -> database.tag2MoveQueries.upsert(
                move_id = moveRowId,
                is_homing = properties.isHoming,
                stance = properties.stance,
                is_high_crush = properties.isHighCrush,
                is_low_crush = properties.isLowCrush,
                has_wall_interaction = properties.hasWallInteraction,
                has_floor_interaction = properties.hasFloorInteraction,
            )

            is T5Properties -> database.tekken5MoveQueries.upsert(
                move_id = moveRowId,
                is_homing = properties.isHoming,
                stance = properties.stance,
                is_high_crush = properties.isHighCrush,
                is_low_crush = properties.isLowCrush,
                has_wall_interaction = properties.hasWallInteraction,
                has_floor_interaction = properties.hasFloorInteraction,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties> = emptyMap()

    override fun loadMoveProperties(characterId: CharacterId): Map<Long, MoveGameProperties> {
        val propertiesByMoveRowId: Map<Long, MoveGameProperties> = when (characterId.game) {
            Game.Tekken8 -> database.tekken8MoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.Tag2 -> database.tag2MoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.Tekken5 -> database.tekken5MoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByMoveRowId
    }


    private companion object {
        const val TAG = "WavuSqlDelightGameProperties"
    }
}

private fun Tekken8_move.toDomain(): T8Properties {
    val properties = T8Properties(
        isHeat = is_heat,
        isHoming = is_homing,
        stance = stance,
        isPowerCrush = is_power_crush,
        isHighCrush = is_high_crush,
        isLowCrush = is_low_crush,
        hasWallInteraction = has_wall_interaction,
        hasFloorInteraction = has_floor_interaction,
    )
    return properties
}

private fun Tag2_move.toDomain(): Tag2Properties {
    val properties = Tag2Properties(
        isHoming = is_homing,
        stance = stance,
        isHighCrush = is_high_crush,
        isLowCrush = is_low_crush,
        hasWallInteraction = has_wall_interaction,
        hasFloorInteraction = has_floor_interaction,
    )
    return properties
}

private fun Tekken5_move.toDomain(): T5Properties {
    val properties = T5Properties(
        isHoming = is_homing,
        stance = stance,
        isHighCrush = is_high_crush,
        isLowCrush = is_low_crush,
        hasWallInteraction = has_wall_interaction,
        hasFloorInteraction = has_floor_interaction,
    )
    return properties
}
