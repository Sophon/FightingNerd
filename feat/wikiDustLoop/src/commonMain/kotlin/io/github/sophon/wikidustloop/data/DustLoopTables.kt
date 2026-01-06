package io.github.sophon.wikidustloop.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object DustLoopTables {
    private val gameToTables = mapOf(
        Game.GGST.id to QueryTable(
            character = TABLE_GGST_CHARACTERS,
            moves = TABLE_GGST_MOVE_LIST,
        ),
        Game.DBFZ.id to QueryTable(
            character = TABLE_DBFZ_CHARACTERS,
            moves = TABLE_DBFZ_MOVE_LIST,
        ),
        Game.GBVSR.id to QueryTable(
            character = TABLE_GBVSR_CHARACTERS,
            moves = TABLE_GBVSR_MOVE_LIST,
        ),
        Game.BBCF to QueryTable(
            character = TABLE_BBCF_CHARACTERS,
            moves = TABLE_BBCF_MOVE_LIST,
        )
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }

    internal const val TABLE_GGST_CHARACTERS = "ggstCharacters"
    internal const val TABLE_GGST_MOVE_LIST = "MoveData_GGST"
    internal const val TABLE_DBFZ_CHARACTERS = "dbfzCharacters"
    internal const val TABLE_DBFZ_MOVE_LIST = "MoveData_DBFZ"
    internal const val TABLE_GBVSR_CHARACTERS = "gbvsrCharacters"
    internal const val TABLE_GBVSR_MOVE_LIST = "MoveData_GBVSR"
    internal const val TABLE_BBCF_CHARACTERS = "bbcfCharacters"
    internal const val TABLE_BBCF_MOVE_LIST = "MoveData_BBCF"
}
