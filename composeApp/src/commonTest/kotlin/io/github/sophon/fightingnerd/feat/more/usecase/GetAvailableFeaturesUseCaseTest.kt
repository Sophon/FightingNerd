package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
internal class GetAvailableFeaturesUseCaseTest {
    private val storePath = "get_available_features_test_${Random.nextInt()}.preferences_pb".toPath()
    private val store = PreferenceDataStoreFactory.createWithPath(
        scope = TestScope(UnconfinedTestDispatcher()),
        produceFile = { storePath },
    )

    @AfterTest
    fun cleanup() {
        FileSystem.SYSTEM.delete(storePath)
    }


    @Test
    fun `usecase returns a properly sorted games`() = runTest {
        // given
        val wavuClient = FakeWikiClient(name = "Wavu Wiki")
        val superComboClient = FakeWikiClient(name = "SuperCombo Wiki")
        val repo = FakeFeatureRepo(
            gameClients = mapOf(
                Game.Tekken8 to wavuClient,
                Game.StreetFighter6 to superComboClient,
                Game.MK1 to superComboClient,
            ),
        )
        val usecase = GetAvailableFeaturesUseCase(repo, store)
        val expectedFeatureOrder = listOf("Wavu Wiki", "SuperCombo Wiki")
        val expectedGameOrder = listOf(Game.Tekken8.id, Game.StreetFighter6.id, Game.MK1.id)

        // when
        val result = usecase.invoke()

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val list = (result as Result.Success).data
        val gameIds = list.flatMap { feature -> feature.gameList.map { game -> game.id } }
        assertThat(list.map { it.featureName }).containsExactly(*expectedFeatureOrder.toTypedArray())
        assertThat(gameIds).containsExactly(*expectedGameOrder.toTypedArray())
    }

    @Test
    fun `usecase reflects disabled preference from store`() = runTest {
        // given
        val wavuClient = FakeWikiClient(name = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        store.edit { prefs -> prefs[featureKey("Wavu Wiki", Game.Tekken8.id)] = false }
        val usecase = GetAvailableFeaturesUseCase(repo, store)

        // when
        val result = usecase.invoke()

        // then
        val list = (result as Result.Success).data
        assertThat(list.first().gameList.first().isEnabled).isFalse()
    }
}
