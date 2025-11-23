package io.github.sophon.wikiwavu.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object WavuTables {
    private val gameTables = mapOf(
        Game.Tekken8.id to QueryTable(
            character = "not used",
            moves = TABLE_T8_MOVE_LIST,
        ),
    )

    fun getTable(gameId: String): QueryTable? = gameTables[gameId]
}

private const val TABLE_T8_MOVE_LIST = "Move"