package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.more.KEY_PREFIX_FEATURE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

internal class LoadEmptyWidgetsUseCase(
    private val featureRepo: CoreFeatureRepo,
    private val store: DataStore<Preferences>,
) {
    fun invoke(): Flow<Result<List<HomeViewState.GameWidget>, AppError>> {
        val gameClientMap = featureRepo.getGameClients()

        val flow = store.data
            .map { preferences ->
                val widgetList = gameClientMap
                    .filter { (game, wikiClient) ->
                        val featureName = wikiClient.featureInfo.name
                        val key = booleanPreferencesKey("${KEY_PREFIX_FEATURE}_${featureName}_${game.id}")
                        preferences[key] ?: true
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
            .catch { throwable ->
                val error = when (throwable) {
                    is IOException -> AppError.IOError
                    else -> AppError.Unknown
                }
                emit(Result.Error(error))
            }

        return flow
    }
}
