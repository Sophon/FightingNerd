package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState.UiFeatureSetting
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
internal class SaveFeatureConfigUseCaseTest {
    private val storePath = "save_feature_config_test_${Random.nextInt()}.preferences_pb".toPath()
    private val store = PreferenceDataStoreFactory.createWithPath(
        scope = TestScope(UnconfinedTestDispatcher()),
        produceFile = { storePath },
    )

    @AfterTest
    fun cleanup() {
        FileSystem.SYSTEM.delete(storePath)
    }


    @Test
    fun `usecase saves feature settings to the store`() = runTest {
        // given
        val wavuClient = FakeWikiClient(featureName = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val usecase = SaveFeatureConfigUseCase(store, repo)
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = listOf(
                    UiFeatureSetting.UiGame(
                        displayName = Game.Tekken8.displayName,
                        id = Game.Tekken8.id,
                        isEnabled = true,
                    ),
                ),
            ),
        )

        // when
        val result = usecase.invoke(featureList)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val featureKey = featureKey("Wavu Wiki", Game.Tekken8.id)
        val savedFlag = store.data.first()[featureKey] ?: false
        assertThat(savedFlag).isTrue()
    }

    @Test
    fun `usecase clears cache for a game that becomes disabled`() = runTest {
        // given
        val wavuClient = FakeWikiClient(featureName = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val usecase = SaveFeatureConfigUseCase(store, repo)
        store.edit { prefs -> prefs[featureKey("Wavu Wiki", Game.Tekken8.id)] = true }
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = listOf(
                    UiFeatureSetting.UiGame(displayName = Game.Tekken8.displayName, id = Game.Tekken8.id, isEnabled = false),
                ),
            ),
        )

        // when
        usecase.invoke(featureList)

        // then
        assertThat(wavuClient.clearCacheCalled).isTrue()
    }

    @Test
    fun `usecase clears cache for a game disabled without a prior stored preference`() = runTest {
        // given
        val wavuClient = FakeWikiClient(featureName = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val usecase = SaveFeatureConfigUseCase(store, repo)
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = listOf(
                    UiFeatureSetting.UiGame(displayName = Game.Tekken8.displayName, id = Game.Tekken8.id, isEnabled = false),
                ),
            ),
        )

        // when
        usecase.invoke(featureList)

        // then
        assertThat(wavuClient.clearCacheCalled).isTrue()
    }

    @Test
    fun `usecase does not clear cache for a game that stays disabled`() = runTest {
        // given
        val wavuClient = FakeWikiClient(featureName = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val usecase = SaveFeatureConfigUseCase(store, repo)
        store.edit { prefs -> prefs[featureKey("Wavu Wiki", Game.Tekken8.id)] = false }
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = listOf(
                    UiFeatureSetting.UiGame(displayName = Game.Tekken8.displayName, id = Game.Tekken8.id, isEnabled = false),
                ),
            ),
        )

        // when
        usecase.invoke(featureList)

        // then
        assertThat(wavuClient.clearCacheCalled).isFalse()
    }
}
