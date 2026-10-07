package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.SubscribeToAvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import io.github.sophon.fightingnerd.inPort.SubscribeToGamesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
internal class SubscribeToGamesService(
    private val subscribeToAvailableGamesPort: SubscribeToAvailableGamesPort,
    private val subscribeToGameSettingsPort: SubscribeToGameSettingsPort,
): SubscribeToGamesUseCase {
    override fun invoke(): Flow<Result<List<Game>, AppError>> {
        val flow = subscribeToAvailableGamesPort.subscribeToAvailableGames()
            .flatMapLatest { availableGameSet ->
                subscribeToGameSettingsPort.subscribeToGameSettings(availableGameSet)
            }
            .map { result ->
                val enabledGameListResult = result.map { availableGameMap ->
                    val enabledGameList = availableGameMap
                        .filterValues { isEnabled -> isEnabled }
                        .keys
                        .toList()
                    enabledGameList
                }
                enabledGameListResult
            }
            .distinctUntilChanged()
        return flow
    }
}
