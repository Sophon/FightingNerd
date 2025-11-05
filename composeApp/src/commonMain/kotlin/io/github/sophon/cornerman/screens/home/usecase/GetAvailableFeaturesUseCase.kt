package io.github.sophon.cornerman.screens.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.github.sophon.core.domain.FeatureInfo
import io.github.sophon.core.domain.Result
import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.cornerman.featureRegistry.FeatureRegistry
import io.github.sophon.cornerman.screens.KEY_PREFIX_FEATURE
import io.github.sophon.cornerman.screens.home.HomeError
import kotlinx.coroutines.flow.first

internal class GetAvailableFeaturesUseCase(
    private val registry: FeatureRegistry,
    private val store: DataStore<Preferences>
) {
    suspend fun invoke(): Result<List<ComposeRegisteredFeature>, HomeError> {
        return getEnabledFeatures(getAvailableFeatures())
    }

    private fun getAvailableFeatures(): List<FeatureInfo> {
        return registry.getFeatures().map { it.featureInfo }
    }

    private suspend fun getEnabledFeatures(
        featureList: List<FeatureInfo>,
    ): Result<List<ComposeRegisteredFeature>, HomeError> {
        return try {
            val preferences = store.data.first()
            val featureSettings = featureList.mapNotNull { featureInfo ->
                val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
                val isEnabled = preferences[key] ?: false
                if (isEnabled) {
                    registry.getFeature(featureInfo)
                } else {
                    null
                }
            }
            Result.Success(featureSettings)
        } catch (_: IOException) {
            Result.Error(HomeError.IO_ERROR)
        } catch (_: Exception) {
            Result.Error(HomeError.UNKNOWN)
        }
    }
}