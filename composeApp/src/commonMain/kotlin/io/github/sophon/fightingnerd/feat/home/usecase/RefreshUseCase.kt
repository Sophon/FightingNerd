package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.core.util.mapWikiError
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class RefreshUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
) {
    fun invoke(): Flow<AppError> {
        val flow = channelFlow {
            val preferences = store.data.first()
            val enabledClients = featureRepo.getGameClients()
                .filter { (game, wikiClient) ->
                    preferences[featureKey(wikiClient.featureInfo.name, game.id)] == true
                }
                .map { it.value }
            enabledClients.forEach { wikiClient ->
                launch {
                    val result = wikiClient.refreshData().mapWikiError()
                    if (result is Result.Error) {
                        send(result.error)
                    }
                }
            }
        }
        return flow
    }
}
