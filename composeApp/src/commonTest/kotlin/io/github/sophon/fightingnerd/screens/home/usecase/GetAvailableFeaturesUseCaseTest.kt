package io.github.sophon.fightingnerd.screens.home.usecase

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.navigation.NavHostController
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.github.sophon.core.feature.FeatureConfig
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.domain.Result
import io.github.sophon.fightingnerd.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.fightingnerd.featureRegistry.FeatureListLoader
import io.github.sophon.fightingnerd.featureRegistry.FeatureRegistry
import io.github.sophon.fightingnerd.screens.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.screens.home.HomeError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test

class GetAvailableFeaturesUseCaseTest {
    // region Happy Path Tests
    @Test
    fun `given features with no preferences when invoke then emits all features enabled by default`() = runTest {
        // Given
        val featureInfo1 = FeatureInfo("Feature1", "url1", "icon1", version = "1.0.0")
        val featureInfo2 = FeatureInfo("Feature2", "url2", "icon2", version = "1.0.0")

        val features = listOf(
            FakeComposeRegisteredFeature(featureInfo1),
            FakeComposeRegisteredFeature(featureInfo2)
        )
        val mockRegistry = createRegistry(features)
        val mockStore = FakeDataStore()
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When/Then
        useCase.invoke().test {
            val result = awaitItem()
            assertThat(result is Result.Success).isTrue()
            val enabledFeatures = (result as Result.Success).data
            assertThat(enabledFeatures.size).isEqualTo(2)
            assertThat(enabledFeatures[0].featureInfo).isEqualTo(featureInfo1)
            assertThat(enabledFeatures[1].featureInfo).isEqualTo(featureInfo2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given features with existing preferences when invoke then emits only enabled features`() = runTest {
        // Given
        val featureInfo1 = FeatureInfo("Feature1", "url1", "icon1", version = "1.0.0")
        val featureInfo2 = FeatureInfo("Feature2", "url2", "icon2", version = "1.0.0")

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

        // When/Then
        useCase.invoke().test {
            val result = awaitItem()
            assertThat(result is Result.Success).isTrue()
            val enabledFeatures = (result as Result.Success).data
            assertThat(enabledFeatures.size).isEqualTo(1)
            assertThat(enabledFeatures[0].featureInfo).isEqualTo(featureInfo2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given empty feature list when invoke then emits empty list`() = runTest {
        // Given
        val mockRegistry = createRegistry(emptyList())
        val mockStore = FakeDataStore()
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When/Then
        useCase.invoke().test {
            val result = awaitItem()
            assertThat(result is Result.Success).isTrue()
            val enabledFeatures = (result as Result.Success).data
            assertThat(enabledFeatures.size).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given all features disabled when invoke then emits empty list`() = runTest {
        // Given
        val featureInfo1 = FeatureInfo("Feature1", "url1", "icon1", version = "1.0.0")
        val featureInfo2 = FeatureInfo("Feature2", "url2", "icon2", version = "1.0.0")

        val features = listOf(
            FakeComposeRegisteredFeature(featureInfo1),
            FakeComposeRegisteredFeature(featureInfo2)
        )
        val mockRegistry = createRegistry(features)

        val initialPreferences = mutablePreferencesOf(
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo1.name) to false,
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo2.name) to false
        )
        val mockStore = FakeDataStore(initialPreferences)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When/Then
        useCase.invoke().test {
            val result = awaitItem()
            assertThat(result is Result.Success).isTrue()
            val enabledFeatures = (result as Result.Success).data
            assertThat(enabledFeatures.size).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }
    // endregion

    // region Reactive Flow Tests
    @Test
    fun `given preferences change when collecting flow then emits updated feature list`() = runTest {
        // Given
        val featureInfo1 = FeatureInfo("Feature1", "url1", "icon1", version = "1.0.0")
        val featureInfo2 = FeatureInfo("Feature2", "url2", "icon2", version = "1.0.0")

        val features = listOf(
            FakeComposeRegisteredFeature(featureInfo1),
            FakeComposeRegisteredFeature(featureInfo2)
        )
        val mockRegistry = createRegistry(features)

        val initialPreferences = mutablePreferencesOf(
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo1.name) to true,
            booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo2.name) to false
        )
        val mockStore = FakeDataStore(initialPreferences, reactive = true)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When/Then
        useCase.invoke().test {
            // First emission
            val result1 = awaitItem()
            assertThat(result1 is Result.Success).isTrue()
            val enabledFeatures1 = (result1 as Result.Success).data
            assertThat(enabledFeatures1.size).isEqualTo(1)
            assertThat(enabledFeatures1[0].featureInfo).isEqualTo(featureInfo1)

            // Update preferences
            mockStore.updatePreferences(
                mutablePreferencesOf(
                    booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo1.name) to false,
                    booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo2.name) to true
                )
            )

            // Second emission
            val result2 = awaitItem()
            assertThat(result2 is Result.Success).isTrue()
            val enabledFeatures2 = (result2 as Result.Success).data
            assertThat(enabledFeatures2.size).isEqualTo(1)
            assertThat(enabledFeatures2[0].featureInfo).isEqualTo(featureInfo2)

            cancelAndIgnoreRemainingEvents()
        }
    }
    // endregion

    // region Error Handling Tests
    @Test
    fun `given datastore throws IOException when invoke then emits IO_ERROR`() = runTest {
        // Given
        val features = listOf(FakeComposeRegisteredFeature(FeatureInfo("Feature", "url", "icon", version = "1.0.0")))
        val mockRegistry = createRegistry(features)
        val mockStore = FakeDataStore(shouldThrowIOException = true)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When/Then
        useCase.invoke().test {
            val result = awaitItem()
            assertThat(result is Result.Error).isTrue()
            val error = (result as Result.Error).error
            assertThat(error).isEqualTo(HomeError.IO_ERROR)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given datastore throws generic exception when invoke then emits UNKNOWN error`() = runTest {
        // Given
        val features = listOf(FakeComposeRegisteredFeature(FeatureInfo("Feature", "url", "icon", version = "1.0.0")))
        val mockRegistry = createRegistry(features)
        val mockStore = FakeDataStore(shouldThrowGenericException = true)
        val useCase = GetAvailableFeaturesUseCase(mockRegistry, mockStore)

        // When/Then
        useCase.invoke().test {
            val result = awaitItem()
            assertThat(result is Result.Error).isTrue()
            val error = (result as Result.Error).error
            assertThat(error).isEqualTo(HomeError.UNKNOWN)
            cancelAndIgnoreRemainingEvents()
        }
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
                FeatureConfig.Feature(name = name, isEnabled = true)
            }
            return FeatureConfig(features)
        }
    }

    private class FakeComposeRegisteredFeature(
        override val featureInfo: FeatureInfo
    ) : ComposeRegisteredFeature {
        @Composable
        override fun HomeScreenContent(navHostController: NavHostController) {}
        override suspend fun onInit() {}
        override suspend fun search(query: String) {}
        override fun subscribeToSearchResults(): Flow<String> = flowOf()
    }

    private class FakeDataStore(
        initialPreferences: Preferences = mutablePreferencesOf(),
        private val reactive: Boolean = false,
        private val shouldThrowIOException: Boolean = false,
        private val shouldThrowGenericException: Boolean = false
    ) : DataStore<Preferences> {
        private val preferencesFlow = MutableStateFlow(initialPreferences)

        override val data: Flow<Preferences>
            get() = flow {
                if (shouldThrowIOException) throw IOException("Test IO Exception")
                if (shouldThrowGenericException) throw RuntimeException("Test Generic Exception")

                if (reactive) {
                    preferencesFlow.collect { emit(it) }
                } else {
                    emit(preferencesFlow.value)
                }
            }

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val newPrefs = transform(preferencesFlow.value)
            preferencesFlow.value = newPrefs
            return newPrefs
        }

        fun updatePreferences(newPreferences: Preferences) {
            preferencesFlow.value = newPreferences
        }
    }
    // endregion
}