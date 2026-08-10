package io.github.sophon.wikiwavu.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikiwavu.data.db.fromDomain
import io.github.sophon.wikiwavu.data.db.toDomain
import io.github.sophon.wikiwavu.data.remote.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.remote.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
            is Result.Error -> {
                val error: EmptyResult<DataError> = Result.Error(remote.error)
                return error
            }
        }
        if (characters.isEmpty()) {
            val empty: EmptyResult<DataError> = Result.Success(Unit)
            return empty
        }
        val result = withContext(Dispatchers.IO) {
            try {
                db.transaction {
                    val remoteIds = characters.map { it.id }
                    characters.forEach { character ->
                        insert(character, gameId = Game.Tekken8.id)
                    }
                    queries.incrementFailureCountForAbsent(remoteIds)
                    queries.deleteExceededThreshold(FAILURE_COUNT_THRESHOLD)
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

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        val flow = queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val characters = rows.map { it.toDomain() }
                characters
            }
        return flow
    }

    override suspend fun wipeData(): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.deleteAll()
                val success: EmptyResult<DataError> = Result.Success(Unit)
                success
            } catch (_: Exception) {
                val error: EmptyResult<DataError> = Result.Error(DataError.Local.UNKNOWN)
                error
            }
        }
        return result
    }


    private fun insert(character: Character, gameId: String) {
        queries.insertCharacter(
            id = character.id,
            gameId = gameId,
            remoteQueryId = character.remoteQueryId,
            wikiUrl = character.wikiUrl,
            displayName = character.displayName,
            aliases = character.aliasList.fromDomain(),
            hp = character.hp,
            umo = character.umo.fromDomain(),
            imagesIconUrl = character.images?.iconUrl,
            imagesBannerUrl = character.images?.bannerUrl,
        )
    }


    private companion object {
        const val FAILURE_COUNT_THRESHOLD = 5L
    }
}
