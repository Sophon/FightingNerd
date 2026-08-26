package io.github.sophon.dreamcancel.data.remote

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object DreamCancelTables {
    private val gameToTables = mapOf(
        Game.KoFXV.id to QueryTable(
            character = TABLE_KOF15_CHARACTERS,
            moves = TABLE_KOF15_MOVES,
        ),
        Game.COTW.id to QueryTable(
            character = TABLE_COTW_CHARACTERS,
            moves = TABLE_COTW_MOVES,
        )
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }

    internal const val TABLE_KOF15_CHARACTERS = "NOT USED"
    internal const val TABLE_KOF15_MOVES = "MoveData_KOFXV"
    internal const val TABLE_COTW_CHARACTERS = "NOT USED"
    internal const val TABLE_COTW_MOVES = "MoveData_COTW"
}