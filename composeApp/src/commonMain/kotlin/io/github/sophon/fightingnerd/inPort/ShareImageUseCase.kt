package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

interface ShareImageUseCase {
    suspend operator fun invoke(pngBytes: ByteArray, fileName: String): EmptyResult<AppError>
}
