package io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.sqldelight.LazyWikiDB
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.COTWMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.KOF15MoveProperties
import io.github.sophon.wiki.data.dreamCancel.Cotw_move
import io.github.sophon.wiki.data.dreamCancel.Kofxv_move

internal class DreamCancelSqlDelightGameProperties(
    wikiDatabase: LazyWikiDB,
) : SqlDelightGameProperties {
    private val database by wikiDatabase

    // KoF XV and COTW characters have no game properties
    override fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    ) = Unit

    override fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    ) {
        when (properties) {
            is KOF15MoveProperties -> database.kofxvMoveQueries.upsert(
                move_id = moveRowId,
                stun = properties.stun,
            )

            is COTWMoveProperties -> database.cotwMoveQueries.upsert(
                move_id = moveRowId,
                rev_damage = properties.revDamage,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties> = emptyMap()

    override fun loadMoveProperties(
        game: Game,
        characterId: CharacterId,
    ): Map<Long, MoveGameProperties> {
        val propertiesByMoveRowId: Map<Long, MoveGameProperties> = when (game) {
            Game.KoFXV -> database.kofxvMoveQueries
                .selectByCharacter(game = game.id, natural_id = characterId.value)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.COTW -> database.cotwMoveQueries
                .selectByCharacter(game = game.id, natural_id = characterId.value)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByMoveRowId
    }


    private companion object {
        const val TAG = "DreamCancelSqlDelightGameProperties"
    }
}

private fun Kofxv_move.toDomain(): KOF15MoveProperties {
    val properties = KOF15MoveProperties(
        stun = stun,
    )
    return properties
}

private fun Cotw_move.toDomain(): COTWMoveProperties {
    val properties = COTWMoveProperties(
        revDamage = rev_damage,
    )
    return properties
}
