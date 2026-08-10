package io.github.sophon.core.wiki.model

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface WikiClient {
    val featureInfo: FeatureInfo
    val supportedGameSet: Set<Game> get() {
        return featureInfo.supportedGameSet
    }

    //new interface
    suspend fun refreshData(): EmptyResult<WikiError>
    fun subscribeToCharacterList(): Flow<List<Character>>
    fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>>

    suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError>
    suspend fun clearCache(): EmptyResult<WikiError>

    fun getFiltersFor(game: Game): Set<Filter>


    //old interface
    suspend fun downloadCharacterList(): Result<List<Character>, WikiError>
    suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError>
    suspend fun fetchCharacterList(): Result<List<Character>, WikiError>
    suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError>

    suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError>
    suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError>
    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError>
    suspend fun fetchMoveList(characterQuery: String, filter: Filter = Filter.None): Result<List<Move>, WikiError>
    suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError>
}
