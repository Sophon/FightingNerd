package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object MizuumiTables {
    private val gameToTables = mapOf(
        Game.MBTL.id to QueryTable(
            character = "NOT USED",
            moves = TABLE_MBTL_MOVES,
        ),
        Game.Uni2.id to QueryTable(
            character = TABLE_UNI2_CHARS,
            moves = TABLE_UNI2_MOVES,
        ),
        Game.VSAV.id to QueryTable(
            character = "NOT USED",
            moves = TABLE_VSAV_MOVES,
        )
    )

    fun getTable(gameId: String): QueryTable? = gameToTables[gameId]

    internal const val TABLE_MBTL_MOVES = "MBTL_MoveData"
    internal const val TABLE_UNI2_CHARS = "UNI2_CharStats"
    internal const val TABLE_UNI2_MOVES = "UNI2_MoveData"
    internal const val TABLE_VSAV_MOVES = "VSAV_MoveData"
}