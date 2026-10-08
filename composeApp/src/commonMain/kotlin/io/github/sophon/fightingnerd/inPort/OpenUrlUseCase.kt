package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

interface OpenUrlUseCase {
    operator fun invoke(url: String): EmptyResult<AppError>
}
