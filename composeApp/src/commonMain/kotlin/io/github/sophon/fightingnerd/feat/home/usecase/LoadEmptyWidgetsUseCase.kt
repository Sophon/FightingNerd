package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

internal class LoadEmptyWidgetsUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
) {
    fun invoke(): Flow<Result<List<HomeViewState.GameWidget>, AppError>> {
        val flow = flow {
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
}
