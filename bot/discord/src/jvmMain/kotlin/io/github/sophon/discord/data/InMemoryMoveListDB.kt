@file:Suppress("DEPRECATION")

package io.github.sophon.discord.data

import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class InMemoryMoveListDB: MoveListDB {
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()
    private var insertTimeInstant: Instant? = null

    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<List<Move>, WikiError> {
        return database[charName]
            ?.let { Result.Success(it.values.toList()) }
            ?: Result.Error(WikiError.UNKNOWN_CHARACTER)
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        val moveList = database[charName]
            ?: return Result.Error(WikiError.UNKNOWN_CHARACTER)
        val moveData = moveList[moveQuery]
            ?: return Result.Error(WikiError.UNKNOWN_MOVE)

        return Result.Success(moveData)
    }

    override suspend fun insertMoveList(
        charName: String,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
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

    override suspend fun wipe(): EmptyResult<WikiError> {
        database.clear()
        insertTimeInstant = null
        return Result.Success(Unit)
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
        return Result.Success(insertTimeInstant)
    }
}