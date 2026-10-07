package io.github.sophon.fightingnerd.core.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class PreferenceRepoImpl(
    private val store: DataStore<Preferences>
): PreferenceRepo {
    override fun subscribeToTheme(): Flow<ThemeMode> {
        val flow = store.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                val raw = preferences[KEY_THEME_MODE]
                val parsed = raw?.let { stringValue ->
                    runCatching { ThemeMode.valueOf(stringValue) }.getOrNull()
                }
                val mode = parsed ?: ThemeMode.System
                return@map mode
            }
        return flow
    }

    override suspend fun setTheme(themeMode: ThemeMode): EmptyResult<AppError> {
        val result = try {
            store.edit { preferences ->
                preferences[KEY_THEME_MODE] = themeMode.name
            }
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(AppError.IOError(e.message.orEmpty()))
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }

        return result
    }

    override fun subscribeToUpdateInterval(): Flow<Duration?> {
        val flow = store.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                val periodMs = preferences[KEY_UPDATE_INTERVAL_MS]
                val duration = periodMs?.milliseconds
                return@map duration
            }
        return flow
    }

    override suspend fun setUpdateInterval(duration: Duration?): EmptyResult<AppError> {
        val result = try {
            store.edit { preferences ->
                if (duration == null) {
                    preferences.remove(KEY_UPDATE_INTERVAL_MS)
                } else {
                    preferences[KEY_UPDATE_INTERVAL_MS] = duration.inWholeMilliseconds
                }
            }
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(AppError.IOError(e.message.orEmpty()))
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }

        return result
    }


    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_UPDATE_INTERVAL_MS = longPreferencesKey("update_interval_ms")
    }
}
