package io.github.sophon.fightingnerd.screens.settings.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.domain.Result
import io.github.sophon.fightingnerd.screens.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.screens.settings.SettingsError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test

class ToggleFeatureUseCaseTest {
    // region Happy Path Tests
    @Test
    fun `given feature when toggle to enabled then stores true in preferences`() = runTest {
        // Given
        val featureInfo = FeatureInfo("TestFeature", "url", "icon", setOf(), "1.0.0")
        val mockStore = FakeDataStore()
        val useCase = ToggleFeatureUseCase(mockStore)

        // When
        val result = useCase.invoke(featureInfo, isEnabled = true)

        // Then
        assertThat(result is Result.Success).isTrue()
        val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
        val preferences = mockStore.getCurrentPreferences()
        assertThat(preferences[key]).isEqualTo(true)
    }

    @Test
    fun `given feature when toggle to disabled then stores false in preferences`() = runTest {
        // Given
        val featureInfo = FeatureInfo("TestFeature", "url", "icon",  setOf(), "1.0.0")
        val initialPreferences = mutablePreferencesOf(
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name) to true
        )
        val mockStore = FakeDataStore(initialPreferences)
        val useCase = ToggleFeatureUseCase(mockStore)

        // When
        val result = useCase.invoke(featureInfo, isEnabled = false)

        // Then
        assertThat(result is Result.Success).isTrue()
        val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
        val preferences = mockStore.getCurrentPreferences()
        assertThat(preferences[key]).isEqualTo(false)
    }

    @Test
    fun `given existing preference when toggle then overwrites previous value`() = runTest {
        // Given
        val featureInfo = FeatureInfo("TestFeature", "url", "icon",  setOf(), "1.0.0")
        val initialPreferences = mutablePreferencesOf(
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name) to false
        )
        val mockStore = FakeDataStore(initialPreferences)
        val useCase = ToggleFeatureUseCase(mockStore)

        // When
        val result = useCase.invoke(featureInfo, isEnabled = true)

        // Then
        assertThat(result is Result.Success).isTrue()
        val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
        val preferences = mockStore.getCurrentPreferences()
        assertThat(preferences[key]).isEqualTo(true)
    }
    // endregion

    // region Error Handling Tests
    @Test
    fun `given datastore throws IOException when toggle then returns IO_ERROR`() = runTest {
        // Given
        val featureInfo = FeatureInfo("TestFeature", "url", "icon", setOf(), "1.0.0")
        val mockStore = FakeDataStore(shouldThrowIOException = true)
        val useCase = ToggleFeatureUseCase(mockStore)

        // When
        val result = useCase.invoke(featureInfo, isEnabled = true)

        // Then
        assertThat(result is Result.Error).isTrue()
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(SettingsError.IO_ERROR)
    }

    @Test
    fun `given datastore throws generic exception when toggle then returns UNKNOWN error`() = runTest {
        // Given
        val featureInfo = FeatureInfo("TestFeature", "url", "icon", setOf(), "1.0.0")
        val mockStore = FakeDataStore(shouldThrowGenericException = true)
        val useCase = ToggleFeatureUseCase(mockStore)

        // When
        val result = useCase.invoke(featureInfo, isEnabled = true)

        // Then
        assertThat(result is Result.Error).isTrue()
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(SettingsError.UNKNOWN)
    }
    // endregion

    // region Test Fakes
    private class FakeDataStore(
        initialPreferences: Preferences = mutablePreferencesOf(),
        private val shouldThrowIOException: Boolean = false,
        private val shouldThrowGenericException: Boolean = false
    ) : DataStore<Preferences> {
        private var currentPreferences = initialPreferences

        override val data: Flow<Preferences>
            get() = flowOf(currentPreferences)

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            if (shouldThrowIOException) throw IOException("Test IO Exception")
            if (shouldThrowGenericException) throw RuntimeException("Test Generic Exception")

            val mutablePrefs = currentPreferences.toMutablePreferences()
            currentPreferences = transform(mutablePrefs.toPreferences())
            return currentPreferences
        }

        suspend fun edit(transform: suspend (MutablePreferences) -> Unit): Preferences {
            if (shouldThrowIOException) throw IOException("Test IO Exception")
            if (shouldThrowGenericException) throw RuntimeException("Test Generic Exception")

            val mutablePrefs = currentPreferences.toMutablePreferences()
            transform(mutablePrefs)
            currentPreferences = mutablePrefs.toPreferences()
            return currentPreferences
        }

        fun getCurrentPreferences(): Preferences {
            return currentPreferences
        }
    }
    // endregion
}