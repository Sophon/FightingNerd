package io.github.sophon.dreamcancel.data

import io.github.sophon.core.wiki.data.QueryTable

internal object DreamCancelTables {
    private val gameToTables = mapOf(
        "King_of_Fighters_15" to QueryTable(
            character = TABLE_KOF15_CHARACTERS,
            moves = TABLE_KOF15_MOVES,
        ),
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }

    fun supportedGames(): Set<String> = gameToTables.keys
}

private const val TABLE_KOF15_CHARACTERS = "NOT USED"
private const val TABLE_KOF15_MOVES = "MoveData_KOFXV"