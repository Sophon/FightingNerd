package io.github.sophon.wikiwavu.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikiwavu.data.db.persist
import io.github.sophon.wikiwavu.data.db.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal interface CharacterRepo {
    suspend fun refreshCharacterList(): EmptyResult<DataError>
    fun subscribeToCharacterList(): Flow<List<Character>>
    suspend fun wipeData(): EmptyResult<DataError>
}

internal class CharacterRepoImpl(
    private val db: WavuDB,
    private val source: WavuWikiDataSource,
): CharacterRepo {
    private val queries = db.characterQueries

    override suspend fun refreshCharacterList(): EmptyResult<DataError> {
        val remote = source.downloadCharacterList()
        val characters = when (remote) {
            is Result.Success -> remote.data.toDomain()
            is Result.Error -> return Result.Error(remote.error)
        }
        return withContext(Dispatchers.IO) {
            try {
                db.transaction {
                    queries.deleteAll()
                    characters.forEach { character ->
                        queries.persist(character, gameId = Game.Tekken8.id)
                    }
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        val flow = queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }
        return flow
    }

    override suspend fun wipeData(): EmptyResult<DataError> {
        return withContext(Dispatchers.IO) {
            try {
                queries.deleteAll()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }
}
