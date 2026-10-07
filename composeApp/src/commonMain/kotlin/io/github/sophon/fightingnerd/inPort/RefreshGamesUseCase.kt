package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

/**
 * Downloads the enabled games of [gameIdSet] and returns once they're done. Fails if any of their data failed.
 */
interface RefreshGamesUseCase {
    suspend operator fun invoke(gameIdSet: Set<String>): EmptyResult<AppError>
}
