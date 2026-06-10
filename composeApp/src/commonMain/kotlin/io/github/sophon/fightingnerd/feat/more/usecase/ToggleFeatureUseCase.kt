package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.feat.more.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.feat.more.SettingsError

internal class ToggleFeatureUseCase(
    private val store: DataStore<Preferences>,
) {
    suspend fun invoke(
        featureName: String,
        gameId: String,
        isEnabled: Boolean,
    ): EmptyResult<SettingsError> {
        val result = try {
            store.edit { prefs ->
                prefs[featureKey(featureName, gameId)] = isEnabled
            }
            Result.Success(Unit)
        } catch (_: IOException) {
            Result.Error(SettingsError.IO_ERROR)
        } catch (_: Exception) {
            Result.Error(SettingsError.UNKNOWN)
        }
        return result
    }

    suspend fun invoke(
        featureName: String,
        gameIdList: List<String>,
        isEnabled: Boolean,
    ): EmptyResult<SettingsError> {
        val result = try {
            store.edit { prefs ->
                gameIdList.forEach { gameId ->
                    prefs[featureKey(featureName, gameId)] = isEnabled
                }
            }
            Result.Success(Unit)
        } catch (_: IOException) {
            Result.Error(SettingsError.IO_ERROR)
        } catch (_: Exception) {
            Result.Error(SettingsError.UNKNOWN)
        }
        return result
    }

    private fun featureKey(featureName: String, gameId: String): Preferences.Key<Boolean> {
        return booleanPreferencesKey("${KEY_PREFIX_FEATURE}_${featureName}_${gameId}")
    }
}
