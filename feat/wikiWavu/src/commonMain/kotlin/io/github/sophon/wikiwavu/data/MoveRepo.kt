package io.github.sophon.wikiwavu.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.data.db.fromDomain
import io.github.sophon.wikiwavu.data.db.toDomain
import io.github.sophon.wikiwavu.data.remote.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.remote.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal interface MoveRepo {
    suspend fun refreshMoveList(character: Character): EmptyResult<DataError>
    fun subscribeToMoveList(character: Character): Flow<List<Move>>
    suspend fun wipeData(): EmptyResult<DataError>
}

internal class MoveRepoImpl(
    private val db: WavuDB,
    private val source: WavuWikiDataSource,
): MoveRepo {
    private val moveQueries = db.moveQueries
    private val tekkenQueries = db.tekkenMoveQueries

    override suspend fun refreshMoveList(character: Character): EmptyResult<DataError> {
        val remote = source.downloadMoveList(table = TABLE_T8_MOVE, character = character)
        val moves = when (remote) {
            is Result.Success -> remote.data.toDomain(character)
            is Result.Error -> {
                val error: EmptyResult<DataError> = Result.Error(remote.error)
                return error
            }
        }
        if (moves.isEmpty()) {
            val empty: EmptyResult<DataError> = Result.Success(Unit)
            return empty
        }
        val result = withContext(Dispatchers.IO) {
            try {
                db.transaction {
                    val remoteMoveIds = moves.map { it.id }
                    moves.forEach { move ->
                        insert(move)
                    }
                    moveQueries.incrementFailureCountForAbsent(character.id, remoteMoveIds)
                    moveQueries.deleteExceededThreshold(character.id, FAILURE_COUNT_THRESHOLD)
                }
                val success: EmptyResult<DataError> = Result.Success(Unit)
                success
            } catch (_: Exception) {
                val error: EmptyResult<DataError> = Result.Error(DataError.Local.UNKNOWN)
                error
            }
        }
        return result
    }

    override fun subscribeToMoveList(character: Character): Flow<List<Move>> {
        val flow = moveQueries.selectTekkenByCharacter(character.id)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val moves = rows.map { it.toDomain() }
                moves
            }
        return flow
    }

    override suspend fun wipeData(): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                moveQueries.deleteAll()
                val success: EmptyResult<DataError> = Result.Success(Unit)
                success
            } catch (_: Exception) {
                val error: EmptyResult<DataError> = Result.Error(DataError.Local.UNKNOWN)
                error
            }
        }
        return result
    }


    private fun insert(move: Move) {
        moveQueries.insertMove(
            id = move.id,
            characterId = move.characterId,
            name = move.name,
            input = move.input,
            damage = move.damage,
            startup = move.startup,
            active = move.active,
            recovery = move.recovery,
            onBlock = move.onBlock,
            onHit = move.onHit,
            onCH = move.onCH,
            guard = move.guard,
            cancel = move.cancel,
            invulnerability = move.invulnerability,
            isThrow = move.isThrow,
            notes = move.notes.fromDomain(),
            aliases = move.aliases.fromDomain(),
            urlsWikiUrl = move.urls.wikiUrl,
            urlsVideoId = move.urls.videoId,
            urlsHitboxImageList = move.urls.hitboxImageList.fromDomain(),
            urlsMoveImageList = move.urls.moveImageList.fromDomain(),
        )
        val t8 = move.t8Properties
        tekkenQueries.insertTekkenMove(
            moveId = move.id,
            isHeat = t8?.isHeat ?: false,
            isHoming = t8?.isHoming ?: false,
            stance = t8?.stance,
            isPowerCrush = t8?.isPowerCrush ?: false,
            isHighCrush = t8?.isHighCrush ?: false,
            isLowCrush = t8?.isLowCrush ?: false,
            hasWallInteraction = t8?.hasWallInteraction ?: false,
            hasFloorInteraction = t8?.hasFloorInteraction ?: false,
        )
    }


    private companion object {
        const val TABLE_T8_MOVE = "Move"
        const val FAILURE_COUNT_THRESHOLD = 5L
    }
}
