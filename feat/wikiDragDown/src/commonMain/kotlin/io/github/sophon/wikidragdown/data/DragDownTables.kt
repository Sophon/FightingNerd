package io.github.sophon.wikidragdown.data

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object DragDownTables {
    private val gameToTables = mapOf(
        Game.ROA2.id to QueryTable(
            character = TABLE_ROA2_CHARACTERS,
            moves = TABLE_ROA2_MOVE_LIST,
        )
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }


    internal const val TABLE_ROA2_CHARACTERS = "ROA2_CharacterData"
    internal const val TABLE_ROA2_MOVE_LIST = "ROA2_MoveMode"
}