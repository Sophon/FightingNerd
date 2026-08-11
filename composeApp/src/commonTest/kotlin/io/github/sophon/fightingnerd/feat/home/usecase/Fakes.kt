package io.github.sophon.fightingnerd.feat.home.usecase

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
import io.github.sophon.core.wiki.model.WikiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class FakeWikiClient(
    name: String = "",
    private val refreshResult: EmptyResult<WikiError> = Result.Success(Unit),
    private val subscribeToCharacterListResult: List<Character> = emptyList(),
    private val subscribeToMoveListResult: List<Move> = emptyList(),
) : WikiClient {
    var refreshCalled = false
        private set

    override val featureInfo = FeatureInfo(name = name, url = "", version = "1.0.0")
    override suspend fun refreshData(): EmptyResult<WikiError> {
        refreshCalled = true
        return refreshResult
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

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = error("not used")
    override suspend fun clearCache(): EmptyResult<WikiError> = error("not used")
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
