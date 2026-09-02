package io.github.sophon.fightingnerd.feat

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.data.MediaRepo
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class FakeWikiClient(
    name: String = "",
    private val refreshEvents: List<RefreshEvent> = listOf(RefreshEvent.Finished(successCount = 0)),
    private val subscribeToCharacterListResult: List<Character> = emptyList(),
    private val subscribeToMoveListResult: List<Move> = emptyList(),
    private val clearCacheResult: EmptyResult<WikiError> = Result.Success(Unit),
) : WikiClient {
    var refreshCalled = false
        private set
    var clearCacheCalled = false
        private set

    override val featureInfo = FeatureInfo(name = name, url = "", version = "1.0.0")

    override fun refreshData(): Flow<RefreshEvent> {
        refreshCalled = true
        return refreshEvents.asFlow()
    }

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        return flow {
            delay(3.seconds)
            emit(subscribeToCharacterListResult)
        }
    }

    override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> {
        return flow {
            delay(3.seconds)
            emit(subscribeToMoveListResult)
        }
    }

    override suspend fun clearCache(): EmptyResult<WikiError> {
        clearCacheCalled = true
        return clearCacheResult
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = error("not used")
    override fun getFiltersFor(game: Game): Set<Filter> = error("not used")
}

internal class FakeFeatureRepo(
    private val gameClients: Map<Game, WikiClient> = emptyMap(),
) : FeatureRepo {
    override fun getWikiClientFor(game: Game): WikiClient? = gameClients[game]
    override fun getGameClients(): Map<Game, WikiClient> = gameClients
    override fun initialize(config: Config): EmptyResult<WikiError> = Result.Success(Unit)
    override fun getOtherFeatures(): List<Config.Feature> = emptyList()
    override fun getEnabledFeatureNames(): Set<String> = emptySet()
}

internal class FakeMediaRepo(): MediaRepo {
    val wipedGameIdList = mutableListOf<String>()

    override fun subscribeToCharsWithOfflineMedia(gameId: String): Flow<Set<CharacterId>> {
        TODO("Not yet implemented")
    }

    override suspend fun save(
        gameId: String,
        characterId: CharacterId,
        media: Move.Urls,
    ): EmptyResult<AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun wipe(gameId: String) {
        wipedGameIdList.add(gameId)
    }

    override suspend fun wipe(gameId: String, characterId: CharacterId) {
        TODO("Not yet implemented")
    }

    override suspend fun createUpdatedUrls(
        gameId: String,
        characterId: CharacterId,
        media: Move.Urls,
    ): Move.Urls {
        TODO("Not yet implemented")
    }
}
