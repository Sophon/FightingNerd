package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import io.github.sophon.fightingnerd.inPort.SubscribeToGameSettingsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
internal class SubscribeToGameSettingsService(
    private val availableGamesPort: AvailableGamesPort,
    private val subscribeToGameSettingsPort: SubscribeToGameSettingsPort,
): SubscribeToGameSettingsUseCase {
    override fun invoke(): Flow<Result<Map<Game, Boolean>, AppError>> {
        val flow = availableGamesPort.subscribe()
            .flatMapLatest { availableGameSet ->
                subscribeToGameSettingsPort.subscribeToGameSettings(availableGameSet)
            }
            .distinctUntilChanged()
        return flow
    }
}
