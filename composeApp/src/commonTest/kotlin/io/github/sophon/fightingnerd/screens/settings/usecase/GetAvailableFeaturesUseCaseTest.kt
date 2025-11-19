package io.github.sophon.fightingnerd.screens.settings.usecase

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.navigation.NavHostController
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.github.sophon.core.feature.FeatureConfig
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.fightingnerd.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.fightingnerd.featureRegistry.FeatureListLoader
import io.github.sophon.fightingnerd.featureRegistry.FeatureRegistry
import io.github.sophon.fightingnerd.screens.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.screens.settings.SettingsError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test

class GetAvailableFeaturesUseCaseTest {
    // region Happy Path Tests
    @Test
    fun `given features with no preferences when invoke then returns all features enabled by default`() = runTest {
        // Given
        val featureInfo1 = FeatureInfo("Feature1", "url1", "icon1", setOf(), "1.0.0")
        val featureInfo2 = FeatureInfo("Feature2", "url2", "icon2", setOf(), "1.0.0")

        val features = listOf(
            FakeComposeRegisteredFeature(featureInfo1),
            FakeComposeRegisteredFeature(featureInfo2)
        )
        val mockRegistry = createRegistry(features)
        val mockStore = FakeDataStore()
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result is Result.Success).isTrue()
        val settings = (result as Result.Success).data
        assertThat(settings.size).isEqualTo(2)
        assertThat(settings[0].featureInfo).isEqualTo(featureInfo1)
        assertThat(settings[0].isEnabled).isTrue()
        assertThat(settings[1].featureInfo).isEqualTo(featureInfo2)
        assertThat(settings[1].isEnabled).isTrue()
    }

    @Test
    fun `given features with existing preferences when invoke then preserves existing preference values`() = runTest {
        // Given
        val featureInfo1 = FeatureInfo("Feature1", "url1", "icon1", setOf(), "1.0.0")
        val featureInfo2 = FeatureInfo("Feature2", "url2", "icon2", setOf(), "1.0.0")

        val features = listOf(
            FakeComposeRegisteredFeature(featureInfo1),
            FakeComposeRegisteredFeature(featureInfo2)
        )
        val mockRegistry = createRegistry(features)

        val initialPreferences = mutablePreferencesOf(
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo1.name) to false,
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo2.name) to true
        )
        val mockStore = FakeDataStore(initialPreferences)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result is Result.Success).isTrue()
        val settings = (result as Result.Success).data
        assertThat(settings.size).isEqualTo(2)
        assertThat(settings[0].isEnabled).isFalse()
        assertThat(settings[1].isEnabled).isTrue()
    }

    @Test
    fun `given empty feature list when invoke then returns empty settings list`() = runTest {
        // Given
        val mockRegistry = createRegistry(emptyList())
        val mockStore = FakeDataStore()
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result is Result.Success).isTrue()
        val settings = (result as Result.Success).data
        assertThat(settings.size).isEqualTo(0)
    }
    // endregion

    // region Preference Initialization Tests
    @Test
    fun `given new features when invoke then initializes preferences with true`() = runTest {
        // Given
        val featureInfo = FeatureInfo("NewFeature", "url", "icon", setOf(), "1.0.0")
        val features = listOf(FakeComposeRegisteredFeature(featureInfo))
        val mockRegistry = createRegistry(features)

        val mockStore = FakeDataStore()
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        useCase.invoke()

        // Then
        val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
        val preferences = mockStore.getCurrentPreferences()
        assertThat(preferences[key]).isEqualTo(true)
    }

    @Test
    fun `given existing feature preference when invoke then does not overwrite it`() = runTest {
        // Given
        val featureInfo = FeatureInfo("ExistingFeature", "url", "icon", setOf(), "1.0.0")
        val features = listOf(FakeComposeRegisteredFeature(featureInfo))
        val mockRegistry = createRegistry(features)

        val initialPreferences = mutablePreferencesOf(
            booleanPreferencesKey("feature_ExistingFeature") to false
        )
        val mockStore = FakeDataStore(initialPreferences)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        useCase.invoke()

        // Then
        val key = booleanPreferencesKey("feature_ExistingFeature")
        val preferences = mockStore.getCurrentPreferences()
        assertThat(preferences[key]).isEqualTo(false)
    }
    // endregion

    // region Error Handling Tests
    @Test
    fun `given datastore throws IOException on edit when invoke then returns IO_ERROR`() = runTest {
        // Given
        val features = listOf(FakeComposeRegisteredFeature(FeatureInfo("Feature", "url", "icon", setOf(), "1.0.0")))
        val mockRegistry = createRegistry(features)
        val mockStore = FakeDataStore(shouldThrowIOException = true)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result is Result.Error).isTrue()
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(SettingsError.IO_ERROR)
    }

    @Test
    fun `given datastore throws generic exception on edit when invoke then returns UNKNOWN error`() = runTest {
        // Given
        val features = listOf(FakeComposeRegisteredFeature(FeatureInfo("Feature", "url", "icon", setOf(), "1.0.0")))
        val mockRegistry = createRegistry(features)
        val mockStore = FakeDataStore(shouldThrowGenericException = true)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result is Result.Error).isTrue()
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(SettingsError.UNKNOWN)
    }

    @Test
    fun `given datastore throws IOException on read when invoke then returns IO_ERROR`() = runTest {
        // Given
        val features = listOf(FakeComposeRegisteredFeature(FeatureInfo("Feature", "url", "icon", setOf(), "1.0.0")))
        val mockRegistry = createRegistry(features)
        val mockStore = FakeDataStore(shouldThrowIOExceptionOnRead = true)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result is Result.Error).isTrue()
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(SettingsError.IO_ERROR)
    }
    // endregion

    // region Test Helpers
    private suspend fun createRegistry(features: List<ComposeRegisteredFeature>): FeatureRegistry {
        val featureNames = features.map { it.featureInfo.name }
        val fakeLoader = FakeFeatureListLoader(featureNames)
        val registry = FeatureRegistry(fakeLoader, features)
        registry.initialize()
        return registry
    }
// endregion

    // region Test Fakes
    private class FakeFeatureListLoader(
        private val enabledFeatureNames: List<String> = emptyList()
    ) : FeatureListLoader {
        override suspend fun loadFeatureList(): FeatureConfig {
            val features = enabledFeatureNames.map { name ->
                FeatureConfig.Feature(name = name, isEnabled = true, supportedGames = listOf())
            }
            return FeatureConfig(features)
        }
    }

    private class FakeComposeRegisteredFeature(
        override val featureInfo: FeatureInfo
    ) : ComposeRegisteredFeature {
        override fun registerGames(enabledGames: List<String>) {}
        override fun getWikiClient(gameId: String): WikiClient? {
            return null
        }

        @Composable
        override fun HomeScreenContent(navHostController: NavHostController) {}
        override suspend fun onInit() {}
        override suspend fun search(query: String) {}
        override fun subscribeToSearchResults(): Flow<String> = flowOf()
    }

    private class FakeDataStore(
        initialPreferences: Preferences = mutablePreferencesOf(),
        private val shouldThrowIOException: Boolean = false,
        private val shouldThrowGenericException: Boolean = false,
        private val shouldThrowIOExceptionOnRead: Boolean = false
    ) : DataStore<Preferences> {
        private var currentPreferences = initialPreferences

        override val data: Flow<Preferences>
            get() {
                if (shouldThrowIOExceptionOnRead) throw IOException("Test IO Exception on Read")
                return flowOf(currentPreferences)
            }

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