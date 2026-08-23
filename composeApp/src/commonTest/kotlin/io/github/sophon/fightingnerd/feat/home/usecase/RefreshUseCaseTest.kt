package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RefreshUseCaseTest {
    private val storePath = "refresh_usecase_test_${Random.nextInt()}.preferences_pb".toPath()
    private val store = PreferenceDataStoreFactory.createWithPath(
        scope = TestScope(UnconfinedTestDispatcher()),
        produceFile = { storePath },
    )

    @AfterTest
    fun cleanup() {
        FileSystem.SYSTEM.delete(storePath)
    }

    @Test
    fun `usecase emits Finished with successCount when the enabled client refresh succeeds`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            name = "Wavu Wiki",
            refreshEvents = listOf(RefreshEvent.Finished(successCount = 5)),
        )
        val game = Game.Tekken8
        store.edit { prefs -> prefs[featureKey("Wavu Wiki", game.id)] = true }
        val usecase = RefreshUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient)),
            store = store,
        )
        val expectedEmissions = listOf(Result.Success(RefreshReport(game = game, successCount = 5)))
        val expectedRefreshCalled = true

        // when
        val emissions = usecase.invoke().toList()
        val refreshCalled = wikiClient.refreshCalled

        //then
        assertThat(emissions).isEqualTo(expectedEmissions)
        assertThat(refreshCalled).isEqualTo(expectedRefreshCalled)
    }

    @Test
    fun `usecase emits Failed then Finished when the enabled client refresh fails`() = runTest {
        // given
        val wikiError = WikiError.UnknownCharacter("test")
        val wikiClient = FakeWikiClient(
            name = "Wavu Wiki",
            refreshEvents = listOf(
                RefreshEvent.Failed(wikiError),
                RefreshEvent.Finished(successCount = 0),
            ),
        )
        val game = Game.Tekken8
        store.edit { prefs -> prefs[featureKey("Wavu Wiki", game.id)] = true }
        val usecase = RefreshUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient)),
            store = store,
        )
        val expected = listOf(
            Result.Error(AppError.WikiError(wikiError.toString())),
            Result.Success(RefreshReport(game = game, successCount = 0)),
        )

        // when
        val emissions = usecase.invoke().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase does not refresh clients whose preference flag is false`() = runTest {
        // given
        val wikiClient = FakeWikiClient(name = "Wavu Wiki")
        val game = Game.Tekken8
        // preference flag intentionally not set — filter treats missing as not-enabled
        val usecase = RefreshUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient)),
            store = store,
        )
        val expectedRefreshCalled = false

        // when
        usecase.invoke().toList()
        val refreshCalled = wikiClient.refreshCalled

        //then
        assertThat(refreshCalled).isEqualTo(expectedRefreshCalled)
    }

    @Test
    fun `usecase emits per-game outcomes for all enabled clients`() = runTest {
        // given
        val failingError = WikiError.UnknownCharacter("bad")
        val failingClient = FakeWikiClient(
            name = "SuperCombo Wiki",
            refreshEvents = listOf(
                RefreshEvent.Failed(failingError),
                RefreshEvent.Finished(successCount = 0),
            ),
        )
        val succeedingClient = FakeWikiClient(
            name = "Wavu Wiki",
            refreshEvents = listOf(RefreshEvent.Finished(successCount = 3)),
        )
        store.edit { prefs ->
            prefs[featureKey("SuperCombo Wiki", Game.StreetFighter6.id)] = true
            prefs[featureKey("Wavu Wiki", Game.Tekken8.id)] = true
        }
        val usecase = RefreshUseCase(
            featureRepo = FakeFeatureRepo(
                mapOf(
                    Game.Tekken8 to succeedingClient,
                    Game.StreetFighter6 to failingClient,
                )
            ),
            store = store,
        )
        val expected = listOf(
            Result.Success(RefreshReport(game = Game.Tekken8, successCount = 3)),
            Result.Error(AppError.WikiError(failingError.toString())),
            Result.Success(RefreshReport(game = Game.StreetFighter6, successCount = 0)),
        )

        // when
        val emissions = usecase.invoke().toList()

        //then — per-client order is preserved but launches interleave; assert set equality
        assertThat(emissions).containsExactlyInAnyOrder(*expected.toTypedArray())
    }
}
