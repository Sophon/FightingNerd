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
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
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
        val wiki = FakeWikiClient(refreshEvents = listOf(RefreshEvent.Finished(successCount = 0)))

        // when
        val result = useCase(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki.refreshDataCallCount).isEqualTo(1)
    }

    @Test
    fun `invoke - multiple wikis all refresh successfully`() = runTest {
        // given
        val wiki1 = FakeWikiClient(refreshEvents = listOf(RefreshEvent.Finished(successCount = 0)))
        val wiki2 = FakeWikiClient(refreshEvents = listOf(RefreshEvent.Finished(successCount = 0)))
        val wiki3 = FakeWikiClient(refreshEvents = listOf(RefreshEvent.Finished(successCount = 0)))

        // when
        val result = useCase(listOf(wiki1, wiki2, wiki3))

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
        val result = useCase(emptyList)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion

    //region Failure Scenarios
    @Test
    fun `invoke - single wiki refresh fails returns mapped error`() = runTest {
        // given
        val wiki = FakeWikiClient(
            refreshEvents = listOf(
                RefreshEvent.Failed(WikiError.DownloadError("Network error")),
                RefreshEvent.Finished(successCount = 0),
            ),
        )

        // when
        val result = useCase(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.DownloadError::class)
        assertThat(wiki.refreshDataCallCount).isEqualTo(1)
    }

    @Test
    fun `invoke - all wikis fail returns first error`() = runTest {
        // given
        val wiki1 = FakeWikiClient(
            refreshEvents = listOf(
                RefreshEvent.Failed(WikiError.DownloadError("Wiki1 failed")),
                RefreshEvent.Finished(successCount = 0),
            ),
        )
        val wiki2 = FakeWikiClient(
            refreshEvents = listOf(
                RefreshEvent.Failed(WikiError.DatabaseError("Wiki2 failed")),
                RefreshEvent.Finished(successCount = 0),
            ),
        )

        // when
        val result = useCase(listOf(wiki1, wiki2))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.DownloadError::class)
    }

    @Test
    fun `invoke - one wiki fails among many still refreshes all and returns error`() = runTest {
        // given
        val wiki1 = FakeWikiClient(refreshEvents = listOf(RefreshEvent.Finished(successCount = 0)))
        val wiki2 = FakeWikiClient(
            refreshEvents = listOf(
                RefreshEvent.Failed(WikiError.DownloadError("Wiki2 failed")),
                RefreshEvent.Finished(successCount = 0),
            ),
        )
        val wiki3 = FakeWikiClient(refreshEvents = listOf(RefreshEvent.Finished(successCount = 0)))

        // when
        val result = useCase(listOf(wiki1, wiki2, wiki3))

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
            refreshEvents = listOf(
                RefreshEvent.Failed(WikiError.DatabaseError("db down")),
                RefreshEvent.Finished(successCount = 0),
            ),
        )

        // when
        val result = useCase(listOf(wiki))

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
            override fun refreshData(): Flow<RefreshEvent> = flow {
                callOrder.add("wiki1")
                emit(RefreshEvent.Finished(successCount = 0))
            }
        }
        val wiki2 = object : WikiClient by FakeWikiClient() {
            override fun refreshData(): Flow<RefreshEvent> = flow {
                callOrder.add("wiki2")
                emit(RefreshEvent.Finished(successCount = 0))
            }
        }
        val wiki3 = object : WikiClient by FakeWikiClient() {
            override fun refreshData(): Flow<RefreshEvent> = flow {
                callOrder.add("wiki3")
                emit(RefreshEvent.Finished(successCount = 0))
            }
        }

        // when
        useCase(listOf(wiki1, wiki2, wiki3))

        // then
        assertThat(callOrder).containsExactly("wiki1", "wiki2", "wiki3")
    }
    //endregion


    //region Test Setup
    private class FakeWikiClient(
        var refreshEvents: List<RefreshEvent> = listOf(RefreshEvent.Finished(successCount = 0)),
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

        override fun refreshData(): Flow<RefreshEvent> {
            _refreshDataCallCount++
            return refreshEvents.asFlow()
        }

        override fun subscribeToCharacterList(): Flow<List<Character>> = emptyFlow()
        override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> = emptyFlow()
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }
    //endregion
}
