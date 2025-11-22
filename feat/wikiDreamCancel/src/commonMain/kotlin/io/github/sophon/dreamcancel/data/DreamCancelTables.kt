package io.github.sophon.dreamcancel.data

import io.github.sophon.core.wiki.data.QueryTable

internal object DreamCancelTables {
    private val gameToTables = mapOf(
        "The_King_of_Fighters_XV" to QueryTable(
            character = TABLE_KOF15_CHARACTERS,
            moves = TABLE_KOF15_MOVES,
        ),
        "Fatal_Fury:_City_of_the_Wolves" to QueryTable(
            character = TABLE_COTW_CHARACTERS,
            moves = TABLE_COTW_MOVES,
        )
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }

    fun supportedGames(): Set<String> = gameToTables.keys

    internal const val TABLE_KOF15_CHARACTERS = "NOT USED"
    internal const val TABLE_KOF15_MOVES = "MoveData_KOFXV"
    internal const val TABLE_COTW_CHARACTERS = "NOT USED"
    internal const val TABLE_COTW_MOVES = "MoveData_COTW"
}