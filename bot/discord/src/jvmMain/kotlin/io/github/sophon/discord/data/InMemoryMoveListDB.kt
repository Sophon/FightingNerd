@file:Suppress("DEPRECATION")

package io.github.sophon.discord.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.model.Move
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.MoveListDB
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class InMemoryMoveListDB: MoveListDB {
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()
    private var insertTimeInstant: Instant? = null

    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<List<Move>, WavuError> {
        return database[charName]
            ?.let { Result.Success(it.values.toList()) }
            ?: Result.Error(WavuError.UNKNOWN_CHARACTER)
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WavuError> {
        val moveList = database[charName]
            ?: return Result.Error(WavuError.UNKNOWN_CHARACTER)
        val moveData = moveList[moveQuery]
            ?: return Result.Error(WavuError.UNKNOWN_MOVE)

        return Result.Success(moveData)
    }

    override suspend fun insertMoveList(
        charName: String,
        moveList: List<Move>,
    ): EmptyResult<WavuError> {
        val indexedMoves = buildMap {
            moveList.forEach { move ->
                put(move.input, move)
                move.aliases.forEach { alias ->
                    put(alias, move)
                }
            }
        }
        database[charName] = indexedMoves
        insertTimeInstant = Clock.System.now()

        return Result.Success(Unit)
    }

    override suspend fun wipe(): EmptyResult<WavuError> {
        database.clear()
        insertTimeInstant = null
        return Result.Success(Unit)
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WavuError> {
        return Result.Success(insertTimeInstant)
    }
}