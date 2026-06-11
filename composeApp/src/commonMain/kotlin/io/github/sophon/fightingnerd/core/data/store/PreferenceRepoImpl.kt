package io.github.sophon.fightingnerd.core.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

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
        } catch (_: IOException) {
            Result.Error(AppError.IOError)
        } catch (_: Exception) {
            Result.Error(AppError.Unknown)
        }

        return result
    }


    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }
}