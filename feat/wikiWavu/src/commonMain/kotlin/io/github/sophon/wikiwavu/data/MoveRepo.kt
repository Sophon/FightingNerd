package io.github.sophon.wikiwavu.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.data.db.persistMove
import io.github.sophon.wikiwavu.data.db.toDomain
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
            is Result.Error -> return Result.Error(remote.error)
        }
        return withContext(Dispatchers.IO) {
            try {
                db.transaction {
                    moveQueries.deleteByCharacter(character.id)
                    moves.forEach { move ->
                        persistMove(move, moveQueries, tekkenQueries)
                    }
                }
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }

    override fun subscribeToMoveList(character: Character): Flow<List<Move>> {
        val flow = moveQueries.selectTekkenByCharacter(character.id)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }
        return flow
    }

    override suspend fun wipeData(): EmptyResult<DataError> {
        return withContext(Dispatchers.IO) {
            try {
                moveQueries.deleteAll()
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }


    private companion object {
        const val TABLE_T8_MOVE = "Move"
    }
}
