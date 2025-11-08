package io.github.sophon.cornerman.screens.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.cornerman.featureRegistry.FeatureRegistry
import io.github.sophon.cornerman.screens.KEY_PREFIX_FEATURE
import io.github.sophon.cornerman.screens.home.HomeError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class GetAvailableFeaturesUseCase(
    private val registry: FeatureRegistry,
    private val store: DataStore<Preferences>
) {
    fun invoke(): Flow<Result<List<ComposeRegisteredFeature>, HomeError>> {
        return store.data
            .map { filterEnabledFeatures(it) }
            .catch { e ->
                emit(
                    when (e) {
                        is IOException -> Result.Error(HomeError.IO_ERROR)
                        else -> Result.Error(HomeError.UNKNOWN)
                    }
                )
            }
    }

    private fun filterEnabledFeatures(
        preferences: Preferences
    ): Result<List<ComposeRegisteredFeature>, HomeError> {
        val featureList = getAvailableFeatures()
        val enabledFeatures = featureList.mapNotNull { featureInfo ->
            val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
            val isEnabled = preferences[key] ?: true
            if (isEnabled) {
                registry.getFeature(featureInfo)
            } else {
                null
            }
        }

        return Result.Success(enabledFeatures)
    }

    private fun getAvailableFeatures(): List<FeatureInfo> {
        return registry.getFeatures().map { it.featureInfo }
    }
}