package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SharePort
import io.github.sophon.fightingnerd.inPort.ShareImageUseCase

@ExcludeFromCoverage("plain port call")
internal class ShareImageService(
    private val sharePort: SharePort,
): ShareImageUseCase {
    override suspend fun invoke(pngBytes: ByteArray, fileName: String): EmptyResult<AppError> {
        return sharePort.shareImage(pngBytes = pngBytes, fileName = fileName)
    }
}
