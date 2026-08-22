package io.github.sophon.core.wiki.model

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface WikiClient {
    val featureInfo: FeatureInfo
    val supportedGameSet: Set<Game> get() {
        return featureInfo.supportedGameSet
    }

    fun refreshData(): Flow<RefreshEvent>
    fun subscribeToCharacterList(): Flow<List<Character>>
    fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>>
    //TODO: fun getMoveCountFor(characterId: CharacterId): Flow<Int> — cheap readiness check to replace subscribeToMoveList(id).map { it.isNotEmpty() } in HomeVM

    suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError>
    suspend fun clearCache(): EmptyResult<WikiError>

    fun getFiltersFor(game: Game): Set<Filter>
    fun getGroupsFor(
        game: Game,
        extras: List<String> = emptyList(),
    ): List<Group> = listOf(Default)
}
