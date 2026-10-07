package io.github.sophon.fightingnerd.adapter.outbound.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DataStoreAdapterTest {
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
        val isEnabledByGame = mapOf(
            Game(id = "Tekken_8", displayName = "Tekken 8", iconUrl = "https://i.imgur.com/Yl6j809.png", wikiName = "Wavu Wiki") to true,
            Game(id = "MBTL", displayName = "Melty Blood: Type Lumina", iconUrl = "https://i.imgur.com/E6O7DMi.png", wikiName = "Mizuumi Wiki") to false,
        )
        val expected = preferencesOf(
            booleanPreferencesKey("settings_feature__Wavu Wiki_Tekken_8") to true,
            booleanPreferencesKey("settings_feature__Mizuumi Wiki_MBTL") to false,
        )

        // when
        adapter.saveGameSettings(isEnabledByGame)

        // then
        assertThat(store.data.first()).isEqualTo(expected)
    }

    @Test
    fun `game settings are read from the existing feature keys`() = runTest {
        // given
        val tekken8 = Game(id = "Tekken_8", displayName = "Tekken 8", iconUrl = "https://i.imgur.com/Yl6j809.png", wikiName = "Wavu Wiki")
        val mbtl = Game(id = "MBTL", displayName = "Melty Blood: Type Lumina", iconUrl = "https://i.imgur.com/E6O7DMi.png", wikiName = "Mizuumi Wiki")
        val ggst = Game(id = "GGST", displayName = "Guilty Gear -Strive-", iconUrl = "https://i.imgur.com/07yTLtj.png", wikiName = "DustLoop Wiki")
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
    fun `unreadable store emits an io error`() = runTest {
        // given
        val tekken8 = Game(id = "Tekken_8", displayName = "Tekken 8", iconUrl = "https://i.imgur.com/Yl6j809.png", wikiName = "Wavu Wiki")
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
