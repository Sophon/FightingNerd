package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * Every available game, mapped to whether it's enabled.
 */
interface SubscribeToGameSettingsUseCase {
    operator fun invoke(): Flow<Result<Map<Game, Boolean>, AppError>>
}
