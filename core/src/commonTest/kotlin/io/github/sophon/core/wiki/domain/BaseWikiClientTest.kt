package io.github.sophon.core.wiki.domain

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.RefreshEvent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class BaseWikiClientTest {
    @Test
    fun `all-success path emits Finished with count matching character list size`() = runTest(UnconfinedTestDispatcher()) {
        val characters = charactersOf("kazuya", "jin", "law")
        val client = createClient(
            characterRepo = FakeCharacterRepo(characters = characters),
            moveRepo = FakeMoveRepo(moveCountResults = { Result.Success(10) }),
        )

        val events = client.refreshData().toList()

        assertThat(events).containsExactly(RefreshEvent.Finished(successCount = 3))
    }

    @Test
    fun `character-list refresh failure emits Failed then Finished(0)`() = runTest(UnconfinedTestDispatcher()) {
        val client = createClient(
            characterRepo = FakeCharacterRepo(refreshResult = Result.Error(DataError.Remote.NO_INTERNET)),
            moveRepo = FakeMoveRepo(),
        )

        val events = client.refreshData().toList()


        val summaries = events.map { it.toString() }
        assertThat(summaries).containsExactly(
            RefreshEvent.Failed(WikiError.DownloadError(DataError.Remote.NO_INTERNET.toString())).toString(),
            RefreshEvent.Finished(successCount = 0).toString(),
        )
    }

    @Test
    fun `partial move-list failures emit Failed per failure and Finished with success count`() = runTest(UnconfinedTestDispatcher()) {
        val characters = charactersOf("kazuya", "jin", "law", "leo")
        val client = createClient(
            characterRepo = FakeCharacterRepo(characters = characters),
            moveRepo = FakeMoveRepo(
                moveCountResults = { character ->
                    when (character.id) {
                        "jin", "leo" -> Result.Error(DataError.Remote.PAGE_NOT_FOUND)
                        else -> Result.Success(5)
                    }
                },
            ),
        )

        val events = client.refreshData().toList()

        // WikiError variants aren't data classes → use toString comparison for value semantics.
        val summaries = events.map { it.toString() }
        val pageNotFound = RefreshEvent.Failed(WikiError.PageNotFound(DataError.Remote.PAGE_NOT_FOUND.toString())).toString()
        assertThat(summaries).containsExactly(
            pageNotFound,
            pageNotFound,
            RefreshEvent.Finished(successCount = 2).toString(),
        )
    }

    @Test
    fun `concurrent callers share one session and observe identical event sequences`() = runTest(UnconfinedTestDispatcher()) {
        val characters = charactersOf("kazuya", "jin")
        val gate = CompletableDeferred<Unit>()
        val characterRepo = FakeCharacterRepo(
            characters = characters,
            gateOnRefresh = gate,
        )
        val client = createClient(
            characterRepo = characterRepo,
            moveRepo = FakeMoveRepo(moveCountResults = { Result.Success(1) }),
        )

        val eventsA = mutableListOf<RefreshEvent>()
        val eventsB = mutableListOf<RefreshEvent>()
        val jobA = launch { client.refreshData().toList(eventsA) }
        val jobB = launch { client.refreshData().toList(eventsB) }
        // Both jobs are now suspended inside the shared producer's gate.await()
        gate.complete(Unit)
        jobA.join()
        jobB.join()

        val expected = listOf(RefreshEvent.Finished(successCount = 2))
        assertThat(eventsA).isEqualTo(expected)
        assertThat(eventsB).isEqualTo(expected)
        assertThat(characterRepo.refreshCallCount).isEqualTo(1)
    }

    @Test
    fun `subsequent call after completion starts a new session`() = runTest(UnconfinedTestDispatcher()) {
        val characters = charactersOf("kazuya")
        val characterRepo = FakeCharacterRepo(characters = characters)
        val client = createClient(
            characterRepo = characterRepo,
            moveRepo = FakeMoveRepo(moveCountResults = { Result.Success(1) }),
        )

        client.refreshData().toList()
        val secondEvents = client.refreshData().toList()

        assertThat(secondEvents).containsExactly(RefreshEvent.Finished(successCount = 1))
        assertThat(characterRepo.refreshCallCount).isEqualTo(2)
    }

    private fun TestScope.createClient(
        characterRepo: CharacterRepo,
        moveRepo: MoveRepo,
    ): TestWikiClient {
        val client = TestWikiClient(
            game = Game.Tekken8,
            featureInfo = FeatureInfo(name = "test", url = "", version = "1.0.0"),
            characterRepo = characterRepo,
            moveRepo = moveRepo,
            scope = backgroundScope,
        )
        return client
    }

    private fun charactersOf(vararg ids: String): List<Character> {
        val list = ids.map { id ->
            val character = Character(id = id, displayName = id, remoteQueryId = id, wikiUrl = "")
            character
        }
        return list
    }

    private class TestWikiClient(
        game: Game,
        featureInfo: FeatureInfo,
        characterRepo: CharacterRepo,
        moveRepo: MoveRepo,
        scope: CoroutineScope,
    ) : BaseWikiClient(
        game = game,
        featureInfo = featureInfo,
        characterRepo = characterRepo,
        moveRepo = moveRepo,
        scope = scope,
    ) {
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }

    private class FakeCharacterRepo(
        characters: List<Character> = emptyList(),
        private val refreshResult: EmptyResult<DataError> = Result.Success(Unit),
        private val gateOnRefresh: CompletableDeferred<Unit>? = null,
    ) : CharacterRepo {
        private val listFlow = MutableStateFlow(characters)
        private var _refreshCallCount = 0
        val refreshCallCount get() = _refreshCallCount

        override suspend fun refreshCharacterList(): EmptyResult<DataError> {
            _refreshCallCount++
            gateOnRefresh?.await()
            return refreshResult
        }

        override fun subscribeToCharacterList(): Flow<List<Character>> = listFlow

        override suspend fun wipeData(): EmptyResult<DataError> = Result.Success(Unit)
        override suspend fun getLastUpdateTimestamp(): Result<Instant?, DataError> = Result.Success(null)
    }

    private class FakeMoveRepo(
        private val moveCountResults: (Character) -> Result<Int, DataError> = { Result.Success(0) },
    ) : MoveRepo {
        override suspend fun refreshMoveList(character: Character): Result<Int, DataError> {
            val result = moveCountResults(character)
            return result
        }

        override fun subscribeToMoveList(characterId: String): Flow<List<Move>> = emptyFlow()
        override suspend fun wipeData(): EmptyResult<DataError> = Result.Success(Unit)
        override suspend fun getLastUpdateTimestamp(): Result<Instant?, DataError> = Result.Success(null)
    }
}
