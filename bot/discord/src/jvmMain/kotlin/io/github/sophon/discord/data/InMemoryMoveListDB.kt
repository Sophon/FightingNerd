@file:Suppress("DEPRECATION")

package io.github.sophon.discord.data

import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class InMemoryMoveListDB: MoveListDB<BotError> {
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()
    private var insertTimeInstant: Instant? = null

    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<List<Move>, BotError> {
        return database[charName]
            ?.let { Result.Success(it.values.toList()) }
            ?: Result.Error(BotError.UNKNOWN_CHARACTER)
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, BotError> {
        val moveList = database[charName]
            ?: return Result.Error(BotError.UNKNOWN_CHARACTER)
        val moveData = moveList[moveQuery]
            ?: return Result.Error(BotError.UNKNOWN_MOVE)

        return Result.Success(moveData)
    }

    override suspend fun insertMoveList(
        charName: String,
        moveList: List<Move>,
    ): EmptyResult<BotError> {
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

    override suspend fun wipe(): EmptyResult<BotError> {
        database.clear()
        insertTimeInstant = null
        return Result.Success(Unit)
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, BotError> {
        return Result.Success(insertTimeInstant)
    }
}