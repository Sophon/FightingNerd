package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.KEY_FIRST_TIME_HOME_INIT_DONE
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
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
internal class CheckIfFirstLaunchUseCaseTest {
    private val storePath = "check_first_launch_test_${Random.nextInt()}.preferences_pb".toPath()
    private val store = PreferenceDataStoreFactory.createWithPath(
        scope = TestScope(UnconfinedTestDispatcher()),
        produceFile = { storePath },
    )
    private val firstTimeFlagKey = booleanPreferencesKey(KEY_FIRST_TIME_HOME_INIT_DONE)

    @AfterTest
    fun cleanup() {
        FileSystem.SYSTEM.delete(storePath)
    }


    @Test
    fun `usecase enables only the defined games on first launch`() = runTest {
        // given
        val featureRepo = FakeFeatureRepo(
            gameClients = mapOf(
                Game.Tekken8 to FakeWikiClient("Wavu Wiki"),
                Game.StreetFighter6 to FakeWikiClient("SuperCombo Wiki"),
                Game.GGST to FakeWikiClient("DustLoop Wiki"),
                Game.MBTL to FakeWikiClient("Mizuumi Wiki"),
            ),
        )
        val usecase = CheckIfFirstLaunchUseCase(featureRepo, store)
        val expectedFirstLaunchFlag = true
        val expectedTekken8Setting = true
        val expectedStreetFighter6Setting = true
        val expectedGgstSetting = true
        val expectedMbtlSetting = false

        // when
        usecase()
        val snapshot = store.data.first()
        val firstLaunchFlag = snapshot[firstTimeFlagKey]
        val tekken8Setting = snapshot[featureKey("Wavu Wiki", Game.Tekken8.id)]
        val streetFighter6Setting = snapshot[featureKey("SuperCombo Wiki", Game.StreetFighter6.id)]
        val ggstSetting = snapshot[featureKey("DustLoop Wiki", Game.GGST.id)]
        val mbtlSetting = snapshot[featureKey("Mizuumi Wiki", Game.MBTL.id)]

        // then
        assertThat(firstLaunchFlag).isEqualTo(expectedFirstLaunchFlag)
        assertThat(tekken8Setting).isEqualTo(expectedTekken8Setting)
        assertThat(streetFighter6Setting).isEqualTo(expectedStreetFighter6Setting)
        assertThat(ggstSetting).isEqualTo(expectedGgstSetting)
        assertThat(mbtlSetting).isEqualTo(expectedMbtlSetting)
    }

    @Test
    fun `usecase keeps preferences unchanged on successive launch`() = runTest {
        // given
        val featureRepo = FakeFeatureRepo()
        val usecase = CheckIfFirstLaunchUseCase(featureRepo, store)
        store.edit { prefs -> prefs[firstTimeFlagKey] = true }
        val expected = store.data.first()

        // when
        usecase()
        val after = store.data.first()

        // then
        assertThat(after).isEqualTo(expected)
    }
}
