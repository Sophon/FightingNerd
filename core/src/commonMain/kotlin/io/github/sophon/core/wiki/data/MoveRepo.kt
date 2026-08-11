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
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface MoveRepo {
    suspend fun refreshMoveList(character: Character): Result<Int, DataError>
    fun subscribeToMoveList(characterId: String): Flow<List<Move>>
    suspend fun wipeData(): EmptyResult<DataError>
    suspend fun getLastUpdateTimestamp(): Result<Instant?, DataError>
}

@OptIn(ExperimentalTime::class)
class MoveRepoImpl(
    private val dbAdapter: MoveDbAdapter,
    private val remoteAdapter: MoveRemoteAdapter,
) : MoveRepo {
    override suspend fun refreshMoveList(character: Character): Result<Int, DataError> {
        val moveList = when (val remote = remoteAdapter.download(character)) {
            is Result.Success -> remote.data
            is Result.Error -> {
                return Result.Error(remote.error)
            }
        }
        if (moveList.isEmpty()) {
            return Result.Success(moveList.size)
        }

        val result = withContext(Dispatchers.IO) {
            try {
                dbAdapter.transaction {
                    val remoteMoveIds = moveList.map { it.id }
                    moveList.forEach { move -> dbAdapter.insert(move) }
                    dbAdapter.incrementFailureCountForAbsent(characterId = character.id, remoteIds = remoteMoveIds)
                    dbAdapter.deleteExceededThreshold(characterId = character.id, threshold = FAILURE_COUNT_THRESHOLD)
                }
                Result.Success(moveList.size)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override fun subscribeToMoveList(characterId: String): Flow<List<Move>> {
        val flow = dbAdapter.selectMovesFlow(characterId)
        return flow
    }

    override suspend fun getLastUpdateTimestamp(): Result<Instant?, DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                val timestamp = dbAdapter.getLastUpdateTimestamp()
                Result.Success(timestamp)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
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
