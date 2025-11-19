package io.github.sophon.wikiwavu.data

import io.github.sophon.core.wiki.data.QueryTable

internal object WavuTables {
    private val gameTables = mapOf(
        "Tekken_8" to QueryTable(
            character = "not used",
            moves = TABLE_T8_MOVE_LIST,
        ),
    )

    fun getTable(gameId: String): QueryTable? = gameTables[gameId]

    fun supportedGames(): Set<String> = gameTables.keys
}

private const val TABLE_T8_MOVE_LIST = "Move"