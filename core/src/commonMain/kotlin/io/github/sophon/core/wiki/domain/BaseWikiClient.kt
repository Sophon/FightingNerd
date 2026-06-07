package io.github.sophon.core.wiki.domain

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
abstract class BaseWikiClient(
    private val game: Game,
    private val featureInfo: FeatureInfo,
    private val characterDB: CharacterListDB,
    private val moveDB: MoveListDB,
) : WikiClient {
    protected abstract suspend fun downloadCharacterList(): Result<List<Character>, WikiError>
    protected abstract suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError>

    final override fun getFeatureInfo(): FeatureInfo {
        return featureInfo
    }

    final override suspend fun downloadAndCacheCharacterList(): EmptyResult<WikiError> {
        val result = downloadCharacterList()
            .map { characterList ->
                characterDB.insertCharacterList(characterList)
                Unit
            }
        return result
    }

    final override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        val result = characterDB.fetchCharacterList()
        return result
    }

    final override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> {
        val result = characterDB.fetchCharacterDataFor(characterQuery)
        return result
    }

    final override suspend fun downloadAndCacheMoveListFor(character: Character): EmptyResult<WikiError> {
        val result = downloadMoveListFor(character)
            .map { moveList ->
                moveDB.insertMoveList(game, character, moveList)
                Unit
            }
        return result
    }

    final override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> {
        val result = moveDB.fetchMoveListFor(characterId = characterQuery)
            .map { moveList -> moveList.filter(filter.predicate) }
        return result
    }

    final override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> {
        val result = moveDB.fetchMoveDataFor(characterId, moveQuery)
        return result
    }

    final override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        val result = moveDB.getLastInsertTimeStamp()
        return result
    }

    final override suspend fun clearCache(): EmptyResult<WikiError> {
        val charResult = characterDB.wipe()
        val moveResult = moveDB.wipe()
        val result = when {
            charResult is Result.Error -> charResult
            moveResult is Result.Error -> moveResult
            else -> Result.Success(Unit)
        }
        return result
    }
}
