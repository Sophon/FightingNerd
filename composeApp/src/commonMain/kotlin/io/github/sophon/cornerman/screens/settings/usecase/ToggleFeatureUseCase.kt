package io.github.sophon.cornerman.screens.settings.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.cornerman.screens.settings.KEY_PREFIX_FEATURE
import io.github.sophon.cornerman.screens.settings.SettingsError

internal class ToggleFeatureUseCase(
    private val store: DataStore<Preferences>,
) {
    suspend fun invoke(
        feature: ComposeRegisteredFeature,
        isEnabled: Boolean,
    ): EmptyResult<SettingsError> {
        return try {
            store.edit {
                val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + feature.featureInfo.name)
                it[key] = isEnabled
            }
            Result.Success(Unit)
        } catch (_: IOException) {
            Result.Error(SettingsError.IO_ERROR)
        } catch (_: Exception) {
            Result.Error(SettingsError.UNKNOWN)
        }
    }
}