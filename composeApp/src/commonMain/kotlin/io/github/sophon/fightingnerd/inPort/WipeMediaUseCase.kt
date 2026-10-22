package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

interface WipeMediaUseCase {
    suspend operator fun invoke(gameId: String, characterId: String): EmptyResult<AppError>
}
