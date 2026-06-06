package io.github.sophon.core.wiki.model

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.wiki.data.WikiError
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface WikiClient {
    fun getFeatureInfo(): FeatureInfo

    suspend fun downloadCharacterList(): Result<List<Character>, WikiError>
    suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError>
    suspend fun fetchCharacterList(): Result<List<Character>, WikiError>
    suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError>

    suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError>
    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError>
    suspend fun fetchMoveList(
        characterQuery: String,
        filter: Filter = Filter.None,
    ): Result<List<Move>, WikiError>
    suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError>

    suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError>
    suspend fun clearCache(): EmptyResult<WikiError>
}
