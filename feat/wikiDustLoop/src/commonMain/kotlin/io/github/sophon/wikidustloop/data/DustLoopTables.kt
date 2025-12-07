package io.github.sophon.wikidustloop.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object DustLoopTables {
    private val gameToTables = mapOf(
        Game.GGST.id to QueryTable(
            character = TABLE_GGST_CHARACTERS,
            moves = TABLE_GGST_MOVE_LIST,
        )
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }
}

private const val TABLE_GGST_CHARACTERS = "ggstCharacters"
private const val TABLE_GGST_MOVE_LIST = "MoveData_GGST"