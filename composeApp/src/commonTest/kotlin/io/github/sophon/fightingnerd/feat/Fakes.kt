package io.github.sophon.fightingnerd.feat

import io.github.sophon.core.architecture.DataError
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
import io.github.sophon.fightingnerd.core.data.ReviewPolicyRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.review.platform.ReviewHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

    override fun subscribeToLastUpdateTimestamp(): Flow<Instant?> = flowOf(null)
    override fun getFiltersFor(game: Game): Set<Filter> = error("not used")
}

internal class FakeMediaRepo : MediaRepo {
    val wipedGameIds = mutableListOf<String>()

    override fun subscribeToCharsWithOfflineMedia(gameId: String): Flow<Set<CharacterId>> = flowOf(emptySet())
    override suspend fun save(gameId: String, characterId: CharacterId, media: Move.Urls): EmptyResult<AppError> = Result.Success(Unit)
    override suspend fun wipe(gameId: String) {
        wipedGameIds.add(gameId)
    }
    override suspend fun wipe(gameId: String, characterId: CharacterId) = Unit
    override suspend fun createUpdatedUrls(gameId: String, characterId: CharacterId, media: Move.Urls): Move.Urls = media
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

@OptIn(ExperimentalTime::class)
internal class FakeReviewPolicyRepo(
    initialTimestamp: Instant? = null,
) : ReviewPolicyRepo {
    private val timestampFlow = MutableStateFlow(initialTimestamp)

    var savedTimestamp: Instant? = null
        private set

    override fun getInstallationTimestamp(): Flow<Instant?> = timestampFlow

    override suspend fun saveInstallationTimestamp(timestamp: Instant): EmptyResult<DataError.Local> {
        savedTimestamp = timestamp
        timestampFlow.value = timestamp
        return Result.Success(Unit)
    }
}

internal class FakeReviewHandler(
    private val result: EmptyResult<AppError> = Result.Success(Unit),
) : ReviewHandler {
    var requestCount = 0
        private set

    override suspend fun requestReview(): EmptyResult<AppError> {
        requestCount++
        return result
    }
}
