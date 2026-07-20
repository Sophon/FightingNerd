package io.github.sophon.fightingnerd.feat.home.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
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
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime

internal class EnsureMoveListIsCachedUseCaseTest {
    private val testCharacter = Character(
        id = "kazuya",
        displayName = "Kazuya",
        remoteQueryId = "Kazuya",
        wikiUrl = "https://wavu.wiki/t/Kazuya",
    )
    private val testMoveList = listOf(
        Move(
            characterId = "kazuya",
            id = "1",
            input = "1",
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya"),
        ),
    )

    @Test
    fun `usecase does not refresh data when move list is cached`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            fetchCharacterResult = Result.Success(testCharacter),
            checkHasCachedMovesResult = Result.Success(true),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wikiClient.downloadMoveListForCalled).isFalse()
        assertThat(wikiClient.cacheMoveListCalled).isFalse()
    }

    @Test
    fun `usecase refreshes data when move list is not cached`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            fetchCharacterResult = Result.Success(testCharacter),
            checkHasCachedMovesResult = Result.Success(false),
            downloadMoveListResult = Result.Success(testMoveList),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wikiClient.downloadMoveListForCalled).isTrue()
        assertThat(wikiClient.cacheMoveListCalled).isTrue()
        assertThat(wikiClient.cachedCharacter).isEqualTo(testCharacter)
        assertThat(wikiClient.cachedMoveList).isEqualTo(testMoveList)
    }

    @Test
    fun `usecase returns error when game does not exist`() = runTest {
        // given
        val repo = FakeFeatureRepo(gameClients = emptyMap())
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(AppError.WikiError(Game.Tekken8.id))
    }

    @Test
    fun `usecase returns error when character does not exist`() = runTest {
        // given
        val fetchCharacterError = WikiError.UnknownCharacter(testCharacter.id)
        val wikiClient = FakeWikiClient(
            fetchCharacterResult = Result.Error(fetchCharacterError),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(AppError.WikiError(fetchCharacterError.toString()))
    }


    //region Test Doubles
    private class FakeFeatureRepo(
        private val gameClients: Map<Game, WikiClient> = emptyMap(),
    ) : FeatureRepo {
        override fun getWikiClientFor(game: Game): WikiClient? = gameClients[game]
        override fun getGameClients(): Map<Game, WikiClient> = gameClients
        override fun initialize(config: Config): EmptyResult<WikiError> = Result.Success(Unit)
        override fun getOtherFeatures(): List<Config.Feature> = emptyList()
        override fun getEnabledFeatureNames(): Set<String> = emptySet()
    }

    @OptIn(ExperimentalTime::class)
    private class FakeWikiClient(
        private val fetchCharacterResult: Result<Character, WikiError> = Result.Success(
            Character(id = "", displayName = "", remoteQueryId = "", wikiUrl = ""),
        ),
        private val checkHasCachedMovesResult: Result<Boolean, WikiError> = Result.Success(true),
        private val downloadMoveListResult: Result<List<Move>, WikiError> = Result.Success(emptyList()),
        private val cacheMoveListResult: EmptyResult<WikiError> = Result.Success(Unit),
    ) : WikiClient {
        var downloadMoveListForCalled = false
            private set
        var cacheMoveListCalled = false
            private set
        var cachedCharacter: Character? = null
            private set
        var cachedMoveList: List<Move>? = null
            private set

        override val featureInfo = FeatureInfo(name = "Fake Wiki", url = "", version = "1.0.0")

        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = error("not used")
        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = error("not used")
        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = error("not used")
        override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = fetchCharacterResult

        override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
            downloadMoveListForCalled = true
            return downloadMoveListResult
        }

        override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = checkHasCachedMovesResult

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
    //endregion
}
