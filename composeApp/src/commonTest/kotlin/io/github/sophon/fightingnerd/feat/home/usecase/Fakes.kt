package io.github.sophon.fightingnerd.feat.home.usecase

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
    private val fetchCharacterResult: Result<Character, WikiError> = Result.Success(
        Character(id = "", displayName = "", remoteQueryId = "", wikiUrl = ""),
    ),
    private val fetchCharacterListResult: Result<List<Character>, WikiError> = Result.Success(emptyList()),
    private val checkHasCachedMovesResult: Result<Boolean, WikiError> = Result.Success(true),
    private val downloadCharacterListResult: Result<List<Character>, WikiError> = Result.Success(emptyList()),
    private val downloadMoveListResult: Result<List<Move>, WikiError> = Result.Success(emptyList()),
    private val cacheMoveListResult: EmptyResult<WikiError> = Result.Success(Unit),
    private val cacheCharacterListResult: EmptyResult<WikiError> = Result.Success(Unit),
) : WikiClient {
    var downloadMoveListForCalled = false
        private set
    var downloadCharacterListCalled = false
        private set
    var cacheCharacterListCalled = false
        private set
    var cacheMoveListCalled = false
        private set
    var cachedCharacterList: List<Character>? = null
        private set
    var cachedCharacter: Character? = null
        private set
    var cachedMoveList: List<Move>? = null
        private set

    override val featureInfo = FeatureInfo(name = "Fake Wiki", url = "", version = "1.0.0")

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        downloadCharacterListCalled = true
        return downloadCharacterListResult
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = fetchCharacterListResult

    override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = fetchCharacterResult

    override suspend fun downloadMoveListFor(
        character: Character,
    ): Result<List<Move>, WikiError> {
        downloadMoveListForCalled = true
        return downloadMoveListResult
    }

    override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = checkHasCachedMovesResult

    override suspend fun cacheCharacterList(
        characterList: List<Character>,
    ): EmptyResult<WikiError> {
        cacheCharacterListCalled = true
        cachedCharacterList = characterList
        return cacheCharacterListResult
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        cacheMoveListCalled = true
        cachedCharacter = character
        cachedMoveList = moveList
        return cacheMoveListResult
    }


    override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> = error("not used")
    override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = error("not used")
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