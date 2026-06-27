package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.KEY_FIRST_TIME_HOME_INIT_DONE
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

internal class LoadEmptyWidgetsUseCase(
    private val featureRepo: CoreFeatureRepo,
    private val store: DataStore<Preferences>,
) {
    fun invoke(): Flow<Result<List<HomeViewState.GameWidget>, AppError>> {
        val flow = flow {
            initializeFirstTimeDefaultsIfNeeded()

            val widgetFlow = store.data
                .map { preferences ->
                    val widgetList = featureRepo.getGameClients()
                        .filter { (game, wikiClient) ->
                            preferences[featureKey(wikiClient.featureInfo.name, game.id)] == true
                        }
                        .map { (game, wikiClient) ->
                            HomeViewState.GameWidget(
                                game = game,
                                featureName = wikiClient.featureInfo.name,
                                isLoading = true,
                            )
                        }
                    val result: Result<List<HomeViewState.GameWidget>, AppError> = Result.Success(widgetList)
                    result
                }

            emitAll(widgetFlow)
        }
            .catch { throwable ->
                val error = when (throwable) {
                    is IOException -> AppError.IOError
                    else -> AppError.Unknown
                }
                emit(Result.Error(error))
            }

        return flow
    }

    private suspend fun initializeFirstTimeDefaultsIfNeeded() {
        val firstTimeFlagKey = booleanPreferencesKey(KEY_FIRST_TIME_HOME_INIT_DONE)
        val snapshot = store.data.first()
        if (snapshot[firstTimeFlagKey] == true) return

        store.edit { prefs ->
            featureRepo.getGameClients().forEach { (game, wikiClient) ->
                prefs[featureKey(wikiClient.featureInfo.name, game.id)] = ENABLED_GAMES_FIRST_TIME.contains(game)
            }
            prefs[firstTimeFlagKey] = true
        }
    }

    private companion object {
        val ENABLED_GAMES_FIRST_TIME = listOf(
            Game.StreetFighter6,
            Game.Tekken8,
            Game.GGST,
        )
    }
}
