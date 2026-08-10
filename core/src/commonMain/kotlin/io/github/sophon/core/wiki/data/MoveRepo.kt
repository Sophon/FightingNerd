package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface MoveRepo {
    suspend fun refreshMoveList(character: Character): EmptyResult<DataError>
    fun subscribeToMoveList(character: Character): Flow<List<Move>>
    suspend fun wipeData(): EmptyResult<DataError>
}

class MoveRepoImpl(
    private val dbAdapter: MoveDbAdapter,
    private val remoteAdapter: MoveRemoteAdapter,
) : MoveRepo {
    override suspend fun refreshMoveList(character: Character): EmptyResult<DataError> {
        val moves = when (val remote = remoteAdapter.download(character)) {
            is Result.Success -> remote.data
            is Result.Error -> {
                return Result.Error(remote.error)
            }
        }
        if (moves.isEmpty()) {
            return Result.Success(Unit)
        }

        val result = withContext(Dispatchers.IO) {
            try {
                dbAdapter.transaction {
                    val remoteMoveIds = moves.map { it.id }
                    moves.forEach { move -> dbAdapter.insert(move) }
                    dbAdapter.incrementFailureCountForAbsent(characterId = character.id, remoteIds = remoteMoveIds)
                    dbAdapter.deleteExceededThreshold(characterId = character.id, threshold = FAILURE_COUNT_THRESHOLD)
                }
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override fun subscribeToMoveList(character: Character): Flow<List<Move>> {
        val flow = dbAdapter.selectMovesFlow(character.id)
        return flow
    }

    override suspend fun wipeData(): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                dbAdapter.deleteAll()
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }


    private companion object {
        const val FAILURE_COUNT_THRESHOLD = 5L
    }
}
