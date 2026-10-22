package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

/**
 * Every enabled game, mapped to its last update - `null` if it was never downloaded.
 */
interface SubscribeToLastUpdatesUseCase {
    operator fun invoke(): Flow<Result<Map<Game, Instant?>, AppError>>
}
