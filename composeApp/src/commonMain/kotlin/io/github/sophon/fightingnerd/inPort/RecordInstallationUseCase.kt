package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

interface RecordInstallationUseCase {
    suspend operator fun invoke(): EmptyResult<AppError>
}
