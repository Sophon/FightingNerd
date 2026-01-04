package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object MizuumiTables {
    private val gameToTables = mapOf(
        Game.MBTL.id to QueryTable(
            character = "NOT USED",
            moves = TABLE_MBTL_MOVES,
        ),
    )

    fun getTable(gameId: String): QueryTable? = gameToTables[gameId]

    internal const val TABLE_MBTL_MOVES = "MBTL_MoveData"
}