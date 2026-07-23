package io.github.sophon.fightingnerd.feat.more.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class FakeWikiClient(
    featureName: String,
    version: String = "1.0.0",
    iconUrl: String? = null,
    private val clearCacheResult: EmptyResult<WikiError> = Result.Success(Unit),
) : WikiClient {
    override val featureInfo = FeatureInfo(name = featureName, url = "", version = version, iconUrl = iconUrl)

    var clearCacheCalled = false
        private set

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = error("not used")
    override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = error("not used")
    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = error("not used")
    override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = error("not used")
    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = error("not used")
    override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = error("not used")
    override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> = error("not used")
    override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> = error("not used")
    override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = error("not used")
    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = error("not used")

    override suspend fun clearCache(): EmptyResult<WikiError> {
        clearCacheCalled = true
        return clearCacheResult
    }

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
