package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface CharacterRepo {
    suspend fun refreshCharacterList(): EmptyResult<DataError>
    fun subscribeToCharacterList(): Flow<List<Character>>
    suspend fun wipeData(): EmptyResult<DataError>
    suspend fun getLastUpdateTimestamp(): Result<Instant?, DataError>
}

@OptIn(ExperimentalTime::class)
class CharacterRepoImpl(
    private val dbAdapter: CharacterDbAdapter,
    private val remoteAdapter: CharacterRemoteAdapter,
) : CharacterRepo {
    override suspend fun refreshCharacterList(): EmptyResult<DataError> {
        val characters = when (val remote = remoteAdapter.download()) {
            is Result.Success -> remote.data
            is Result.Error -> {
                return Result.Error(remote.error)
            }
        }
        if (characters.isEmpty()) {
            return Result.Success(Unit)
        }

        val result = withContext(Dispatchers.IO) {
            try {
                dbAdapter.transaction {
                    val remoteIds = characters.map { it.id }
                    characters.forEach { character -> dbAdapter.insert(character) }
                    dbAdapter.incrementFailureCountForAbsent(remoteIds)
                    dbAdapter.deleteExceededThreshold(FAILURE_COUNT_THRESHOLD)
                }
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        val flow = dbAdapter.selectAllFlow()
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
