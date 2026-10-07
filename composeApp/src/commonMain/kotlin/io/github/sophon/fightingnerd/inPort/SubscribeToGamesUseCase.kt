package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

interface SubscribeToGamesUseCase {
    operator fun invoke(): Flow<Result<List<Game>, AppError>>
}
