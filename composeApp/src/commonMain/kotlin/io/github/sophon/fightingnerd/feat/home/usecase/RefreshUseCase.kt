package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class RefreshUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
) {
    fun invoke(): Flow<Result<RefreshReport, AppError>> {
        val flow = channelFlow {
            val preferences = store.data.first()
            val enabledGameClients = featureRepo.getGameClients()
                .filter { (game, wikiClient) ->
                    preferences[featureKey(wikiClient.featureInfo.name, game.id)] == true
                }
            enabledGameClients.forEach { (game, wikiClient) ->
                launch {
                    wikiClient.refreshData().collect { event ->
                        when (event) {
                            is RefreshEvent.Failed -> {
                                send(Result.Error(AppError.WikiError(event.error.toString())))
                            }
                            is RefreshEvent.Finished -> {
                                send(Result.Success(RefreshReport(game = game, successCount = event.successCount)))
                            }
                        }
                    }
                }
            }
        }
        return flow
    }
}

internal data class RefreshReport(
    val game: Game,
    val successCount: Int,
)
