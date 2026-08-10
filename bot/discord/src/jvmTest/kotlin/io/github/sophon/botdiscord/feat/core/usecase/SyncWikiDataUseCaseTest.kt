package io.github.sophon.botdiscord.feat.core.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SyncWikiDataUseCaseTest {
    private val useCase = SyncWikiDataUseCase()

    //region Success Scenarios
    @Test
    fun `invoke - single wiki refresh succeeds`() = runTest {
        // given
        val wiki = FakeWikiClient(refreshDataResult = Result.Success(Unit))

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki.refreshDataCallCount).isEqualTo(1)
    }

    @Test
    fun `invoke - multiple wikis all refresh successfully`() = runTest {
        // given
        val wiki1 = FakeWikiClient(refreshDataResult = Result.Success(Unit))
        val wiki2 = FakeWikiClient(refreshDataResult = Result.Success(Unit))
        val wiki3 = FakeWikiClient(refreshDataResult = Result.Success(Unit))

        // when
        val result = useCase.invoke(listOf(wiki1, wiki2, wiki3))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki1.refreshDataCallCount).isEqualTo(1)
        assertThat(wiki2.refreshDataCallCount).isEqualTo(1)
        assertThat(wiki3.refreshDataCallCount).isEqualTo(1)
    }

    @Test
    fun `invoke - empty wiki list returns success`() = runTest {
        // given
        val emptyList = emptyList<WikiClient>()

        // when
        val result = useCase.invoke(emptyList)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion

    //region Failure Scenarios
    @Test
    fun `invoke - single wiki refresh fails returns mapped error`() = runTest {
        // given
        val wiki = FakeWikiClient(
            refreshDataResult = Result.Error(WikiError.DownloadError("Network error")),
        )

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.DownloadError::class)
        assertThat(wiki.refreshDataCallCount).isEqualTo(1)
    }

    @Test
    fun `invoke - all wikis fail returns first error`() = runTest {
        // given
        val wiki1 = FakeWikiClient(
            refreshDataResult = Result.Error(WikiError.DownloadError("Wiki1 failed")),
        )
        val wiki2 = FakeWikiClient(
            refreshDataResult = Result.Error(WikiError.DatabaseError("Wiki2 failed")),
        )

        // when
        val result = useCase.invoke(listOf(wiki1, wiki2))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.DownloadError::class)
    }

    @Test
    fun `invoke - one wiki fails among many still refreshes all and returns error`() = runTest {
        // given
        val wiki1 = FakeWikiClient(refreshDataResult = Result.Success(Unit))
        val wiki2 = FakeWikiClient(
            refreshDataResult = Result.Error(WikiError.DownloadError("Wiki2 failed")),
        )
        val wiki3 = FakeWikiClient(refreshDataResult = Result.Success(Unit))

        // when
        val result = useCase.invoke(listOf(wiki1, wiki2, wiki3))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.DownloadError::class)
        assertThat(wiki1.refreshDataCallCount).isEqualTo(1)
        assertThat(wiki2.refreshDataCallCount).isEqualTo(1)
        assertThat(wiki3.refreshDataCallCount).isEqualTo(1)
    }

    @Test
    fun `invoke - DatabaseError is mapped to Unknown BotError`() = runTest {
        // given
        val wiki = FakeWikiClient(
            refreshDataResult = Result.Error(WikiError.DatabaseError("db down")),
        )

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.Unknown::class)
    }
    //endregion

    //region Ordering
    @Test
    fun `invoke - refreshes wikis in order provided`() = runTest {
        // given
        val callOrder = mutableListOf<String>()
        val wiki1 = object : WikiClient by FakeWikiClient() {
            override suspend fun refreshData(): EmptyResult<WikiError> {
                callOrder.add("wiki1")
                return Result.Success(Unit)
            }
        }
        val wiki2 = object : WikiClient by FakeWikiClient() {
            override suspend fun refreshData(): EmptyResult<WikiError> {
                callOrder.add("wiki2")
                return Result.Success(Unit)
            }
        }
        val wiki3 = object : WikiClient by FakeWikiClient() {
            override suspend fun refreshData(): EmptyResult<WikiError> {
                callOrder.add("wiki3")
                return Result.Success(Unit)
            }
        }

        // when
        useCase.invoke(listOf(wiki1, wiki2, wiki3))

        // then
        assertThat(callOrder).containsExactly("wiki1", "wiki2", "wiki3")
    }
    //endregion


    //region Test Setup
    private class FakeWikiClient(
        var refreshDataResult: EmptyResult<WikiError> = Result.Success(Unit),
    ) : WikiClient {
        override val featureInfo: FeatureInfo = FeatureInfo(
            name = "wavu",
            url = "",
            version = "",
            supportedGameSet = setOf(),
            iconUrl = "",
        )

        private var _refreshDataCallCount = 0
        val refreshDataCallCount get() = _refreshDataCallCount

        override suspend fun refreshData(): EmptyResult<WikiError> {
            _refreshDataCallCount++
            return refreshDataResult
        }

        override fun subscribeToCharacterList(): Flow<List<Character>> = emptyFlow()
        override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> = emptyFlow()
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = throw NotImplementedError("Not used in this use case")
    }
    //endregion
}
