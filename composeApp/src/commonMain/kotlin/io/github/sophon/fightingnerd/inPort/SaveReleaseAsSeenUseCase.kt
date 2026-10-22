package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

interface SaveReleaseAsSeenUseCase {
    suspend operator fun invoke(version: String): EmptyResult<AppError>
}
