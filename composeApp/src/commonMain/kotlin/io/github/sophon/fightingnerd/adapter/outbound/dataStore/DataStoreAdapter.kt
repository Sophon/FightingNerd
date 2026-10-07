package io.github.sophon.fightingnerd.adapter.outbound.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.KEY_HAS_LAUNCHED_BEFORE
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import io.github.sophon.fightingnerd.feat.more.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class DataStoreAdapter(
    private val store: DataStore<Preferences>,
): FirstLaunchPort, SaveGameSettingsPort, EnabledGamesPort, SubscribeToGameSettingsPort {
    override suspend fun hasLaunchedBefore(): Result<Boolean, AppError> {
        val result = try {
            val preferences = store.data.first()
            Result.Success(preferences[HAS_LAUNCHED_BEFORE_KEY] == true)
        } catch (e: IOException) {
            Result.Error(AppError.IOError(e.message.orEmpty()))
        }
        return result
    }

    override suspend fun markLaunched(): EmptyResult<AppError> {
        val result = edit { preferences ->
            preferences[HAS_LAUNCHED_BEFORE_KEY] = true
        }
        return result
    }

    override suspend fun saveGameSettings(
        composeConfig: ComposeConfig,
        enabledGameIdSet: Set<String>,
    ): EmptyResult<AppError> {
        val result = edit { preferences ->
            composeConfig.availableFeatureList.forEach { feature ->
                feature.supportedGames.forEach { gameId ->
                    preferences[featureKey(feature.name, gameId)] = (gameId in enabledGameIdSet)
                }
            }
        }
        return result
    }

    override suspend fun load(): Result<Set<String>, AppError> {
        val result = try {
            val preferences = store.data.first()
            // key is "${KEY_PREFIX_FEATURE}_${featureName}_${gameId}" - wiki names have no '_', game ids do
            val enabledGameIdSet = preferences.asMap()
                .filter { (key, value) -> key.name.startsWith(KEY_PREFIX_FEATURE) && value == true }
                .map { (key, _) -> key.name.removePrefix("${KEY_PREFIX_FEATURE}_").substringAfter('_') }
                .toSet()
            Result.Success(enabledGameIdSet)
        } catch (e: IOException) {
            Result.Error(AppError.IOError(e.message.orEmpty()))
        }
        return result
    }

    override fun subscribeToGameSettings(gameSet: Set<Game>): Flow<Result<Map<Game, Boolean>, AppError>> {
        val flow = store.data
            .map { preferences ->
                val isEnabledByGame = gameSet.associateWith { game ->
                    preferences[featureKey(game.wikiName, game.id)] == true
                }
                val result: Result<Map<Game, Boolean>, AppError> = Result.Success(isEnabledByGame)
                result
            }
            .catch { throwable ->
                if (throwable !is IOException) throw throwable
                emit(Result.Error(AppError.IOError(throwable.message.orEmpty())))
            }
        return flow
    }


    private suspend fun edit(transform: (MutablePreferences) -> Unit): EmptyResult<AppError> {
        val result = try {
            store.edit { preferences -> transform(preferences) }
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(AppError.IOError(e.message.orEmpty()))
        }
        return result
    }
}


private val HAS_LAUNCHED_BEFORE_KEY = booleanPreferencesKey(KEY_HAS_LAUNCHED_BEFORE)
