package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

internal interface SharePort {
    suspend fun shareImage(pngBytes: ByteArray, fileName: String): EmptyResult<AppError>
}
