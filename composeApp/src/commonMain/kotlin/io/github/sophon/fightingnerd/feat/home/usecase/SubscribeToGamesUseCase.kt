package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

@ExcludeFromCoverage("UI")
internal class SubscribeToGamesUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
) {
    operator fun invoke(): Flow<Result<List<Pair<Game, FeatureInfo>>, AppError>> {
        val flow = store.data
            .map { preferences ->
                val enabledPairs = featureRepo.getGameClients()
                    .filter { (game, wikiClient) ->
                        preferences[featureKey(wikiClient.featureInfo.name, game.id)] == true
                    }
                    .map { (game, wikiClient) ->
                        Pair(game, wikiClient.featureInfo)
                    }
                val result: Result<List<Pair<Game, FeatureInfo>>, AppError> = Result.Success(enabledPairs)
                result
            }
            .catch { throwable ->
                val error = when (throwable) {
                    is IOException -> AppError.IOError(throwable.message.orEmpty())
                    else -> AppError.Unknown
                }
                emit(Result.Error(error))
            }

        return flow
    }
}
