package io.github.sophon.fightingnerd.screens.settings.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.fightingnerd.feat.module.ModuleRepo
import io.github.sophon.fightingnerd.screens.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.screens.settings.SettingsError
import io.github.sophon.fightingnerd.screens.settings.ui.SettingsViewState
import kotlinx.coroutines.flow.first

internal class GetAvailableFeaturesUseCase(
    private val moduleRepo: ModuleRepo,
    private val store: DataStore<Preferences>,
) {
    suspend fun invoke(): Result<List<SettingsViewState.FeatureSetting>, SettingsError> {
        val featureList = moduleRepo.getEnabledModules().map { it.featureInfo }
        return when (val result = updatePreferences(featureList)) {
            is Result.Success -> getFeatureSettings(featureList)
            is Result.Error -> result
        }
    }

    private suspend fun updatePreferences(
        featureList: List<FeatureInfo>
    ): EmptyResult<SettingsError> {
        return try {
            store.edit { preferences ->
                featureList.forEach { featureInfo ->
                    val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
                    if (key !in preferences) {
                        preferences[key] = true
                    }
                }
            }
            Result.Success(Unit)
        } catch (_: IOException) {
            Result.Error(SettingsError.IO_ERROR)
        } catch (_: Exception) {
            Result.Error(SettingsError.UNKNOWN)
        }
    }

    private suspend fun getFeatureSettings(
        featureList: List<FeatureInfo>,
    ): Result<List<SettingsViewState.FeatureSetting>, SettingsError> {
        return try {
            val preferences = store.data.first()
            val featureSettings = featureList.map { featureInfo ->
                val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
                val isEnabled = preferences[key] ?: true
                SettingsViewState.FeatureSetting(featureInfo, isEnabled)
            }
            Result.Success(featureSettings)
        } catch (_: IOException) {
            Result.Error(SettingsError.IO_ERROR)
        } catch (_: Exception) {
            Result.Error(SettingsError.UNKNOWN)
        }
    }
}