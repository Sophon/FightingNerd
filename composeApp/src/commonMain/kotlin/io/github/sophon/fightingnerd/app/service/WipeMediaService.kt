package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.inPort.WipeMediaUseCase

@ExcludeFromCoverage("plain port call")
internal class WipeMediaService(
    private val mediaPort: MediaPort,
): WipeMediaUseCase {
    override suspend fun invoke(gameId: String, characterId: String): EmptyResult<AppError> {
        val result = mediaPort.wipe(gameId, characterId)
        return result
    }
}
