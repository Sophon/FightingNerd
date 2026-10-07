package io.github.sophon.fightingnerd.adapter.outbound.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Wiki
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class DataStoreAdapterTest {
    private val composeConfig = ComposeConfig(
        featureList = listOf(
            ComposeConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
            ComposeConfig.Feature(name = "Mizuumi Wiki", isEnabled = true, supportedGames = listOf("MBTL")),
            ComposeConfig.Feature(name = "DustLoop Wiki", isEnabled = false, supportedGames = listOf("GGST")),
        ),
    )

    @Test
    fun `fresh install has not launched before`() = runTest {
        // given
        val adapter = DataStoreAdapter(fakeStore())
        val expected = Result.Success(false)

        // when
        val result = adapter.hasLaunchedBefore()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `existing install keeps its launch flag`() = runTest {
        // given
        val adapter = DataStoreAdapter(fakeStore(booleanPreferencesKey("has_launched_before") to true))
        val expected = Result.Success(true)

        // when
        val result = adapter.hasLaunchedBefore()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `marked launch is remembered`() = runTest {
        // given
        val adapter = DataStoreAdapter(fakeStore())
        val expected = Result.Success(true)

        // when
        adapter.markLaunched()
        val result = adapter.hasLaunchedBefore()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `game settings are saved under the existing feature keys`() = runTest {
        // given
        val store = fakeStore()
        val adapter = DataStoreAdapter(store)
        val expected = preferencesOf(
            booleanPreferencesKey("settings_feature__Wavu Wiki_Tekken_8") to true,
            booleanPreferencesKey("settings_feature__Mizuumi Wiki_MBTL") to false,
        )

        // when
        adapter.saveGameSettings(composeConfig, enabledGameIdSet = setOf("Tekken_8"))

        // then
        assertThat(store.data.first()).isEqualTo(expected)
    }

    @Test
    fun `enabled games are read from the existing feature keys`() = runTest {
        // given
        val adapter = DataStoreAdapter(
            fakeStore(
                booleanPreferencesKey("settings_feature__Wavu Wiki_Tekken_8") to true,
                booleanPreferencesKey("settings_feature__Mizuumi Wiki_MBTL") to false,
            )
        )
        val expected = Result.Success(setOf("Tekken_8"))

        // when
        val result = adapter.subscribe(composeConfig).first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `enabled games of features disabled in the config are left out`() = runTest {
        // given
        val adapter = DataStoreAdapter(
            fakeStore(
                booleanPreferencesKey("settings_feature__Wavu Wiki_Tekken_8") to true,
                booleanPreferencesKey("settings_feature__DustLoop Wiki_GGST") to true,
            )
        )
        val expected = Result.Success(setOf("Tekken_8"))

        // when
        val result = adapter.subscribe(composeConfig).first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unrelated preference changes don't emit the enabled games again`() = runTest {
        // given
        val store = fakeStore(booleanPreferencesKey("settings_feature__Wavu Wiki_Tekken_8") to true)
        val adapter = DataStoreAdapter(store)
        val expected = Result.Success(setOf("Tekken_8", "MBTL"))

        adapter.subscribe(composeConfig).test {
            awaitItem()

            // when
            adapter.markLaunched()
            store.edit { preferences -> preferences[booleanPreferencesKey("settings_feature__Mizuumi Wiki_MBTL")] = true }

            // then
            val result = awaitItem()
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun `game settings are read from the existing feature keys`() = runTest {
        // given
        val tekken8 = Game(id = "Tekken_8", displayName = "Tekken 8", iconUrl = "https://i.imgur.com/Yl6j809.png", wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"))
        val mbtl = Game(id = "MBTL", displayName = "Melty Blood: Type Lumina", iconUrl = "https://i.imgur.com/E6O7DMi.png", wiki = Wiki(name = "Mizuumi Wiki", url = "https://mizuumi.wiki", iconUrl = "https://mizuumi.wiki/mizulogo.png?1fe5d"))
        val ggst = Game(id = "GGST", displayName = "Guilty Gear -Strive-", iconUrl = "https://i.imgur.com/07yTLtj.png", wiki = Wiki(name = "DustLoop Wiki", url = "https://www.dustloop.com/wiki/", iconUrl = "https://www.dustloop.com/wiki/images/archive/3/30/20260601135625%21Dustloop_Wiki.png"))
        val adapter = DataStoreAdapter(
            fakeStore(
                booleanPreferencesKey("settings_feature__Wavu Wiki_Tekken_8") to true,
                booleanPreferencesKey("settings_feature__Mizuumi Wiki_MBTL") to false,
            )
        )
        val expected = Result.Success(mapOf(tekken8 to true, mbtl to false, ggst to false))

        // when
        val result = adapter.subscribeToGameSettings(setOf(tekken8, mbtl, ggst)).first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `installation timestamp is read from the existing key`() = runTest {
        // given
        val timestamp = Instant.fromEpochMilliseconds(1_759_276_800_000)
        val adapter = DataStoreAdapter(fakeStore(longPreferencesKey("installation_timestamp") to timestamp.toEpochMilliseconds()))
        val expected = Result.Success(timestamp)

        // when
        val result = adapter.getInstallationTimestamp()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `saved installation timestamp is remembered`() = runTest {
        // given
        val timestamp = Instant.fromEpochMilliseconds(1_759_276_800_000)
        val adapter = DataStoreAdapter(fakeStore())
        val expected = Result.Success(timestamp)

        // when
        adapter.saveInstallationTimestamp(timestamp)
        val result = adapter.getInstallationTimestamp()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unreadable store emits an io error`() = runTest {
        // given
        val tekken8 = Game(id = "Tekken_8", displayName = "Tekken 8", iconUrl = "https://i.imgur.com/Yl6j809.png", wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"))
        val adapter = DataStoreAdapter(unreadableStore("corrupted preferences"))
        val expected = Result.Error(AppError.IOError("corrupted preferences"))

        // when
        val result = adapter.subscribeToGameSettings(setOf(tekken8)).first()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun unreadableStore(message: String): DataStore<Preferences> {
        return object : DataStore<Preferences> {
            override val data: Flow<Preferences> = flow { throw IOException(message) }
            override suspend fun updateData(
                transform: suspend (Preferences) -> Preferences,
            ): Preferences {
                throw IOException(message)
            }
        }
    }

    private fun fakeStore(vararg pairs: Preferences.Pair<*>): DataStore<Preferences> {
        val state = MutableStateFlow(preferencesOf(*pairs))
        return object : DataStore<Preferences> {
            override val data: Flow<Preferences> = state
            override suspend fun updateData(
                transform: suspend (Preferences) -> Preferences,
            ): Preferences {
                state.update { current -> transform(current) }
                return state.value
            }
        }
    }
}
