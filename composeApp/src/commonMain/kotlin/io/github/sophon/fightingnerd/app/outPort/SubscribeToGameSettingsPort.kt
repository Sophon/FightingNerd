package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

internal interface SubscribeToGameSettingsPort {
    fun subscribeToGameSettings(gameSet: Set<Game>): Flow<Result<Map<Game, Boolean>, AppError>>
}
