package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.LastUpdatePort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import io.github.sophon.fightingnerd.inPort.SubscribeToLastUpdatesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class SubscribeToLastUpdatesService(
    private val availableGamesPort: AvailableGamesPort,
    private val subscribeToGameSettingsPort: SubscribeToGameSettingsPort,
    private val lastUpdatePort: LastUpdatePort,
): SubscribeToLastUpdatesUseCase {
    override fun invoke(): Flow<Result<Map<Game, Instant?>, AppError>> {
        val flow = availableGamesPort.subscribe()
            .flatMapLatest { availableGameSet ->
                subscribeToGameSettingsPort.subscribeToGameSettings(availableGameSet)
            }
            .distinctUntilChanged()
            .flatMapLatest { settingsResult ->
                when (settingsResult) {
                    is Result.Success -> {
                        val enabledGameList = settingsResult.data
                            .filterValues { isEnabled -> isEnabled }
                            .keys
                            .toList()
                        subscribeToLastUpdates(enabledGameList).map { lastUpdateByGame -> Result.Success(lastUpdateByGame) }
                    }
                    is Result.Error -> flowOf(settingsResult)
                }
            }
        return flow
    }

    private fun subscribeToLastUpdates(gameList: List<Game>): Flow<Map<Game, Instant?>> {
        if (gameList.isEmpty()) return flowOf(emptyMap())

        val flowList = gameList.map { game -> lastUpdatePort.subscribeToLastUpdate(game.id) }
        val flow = combine(flowList) { lastUpdateArray ->
            val lastUpdateByGame = gameList.zip(lastUpdateArray).toMap()
            lastUpdateByGame
        }
        return flow
    }
}
