package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.LastSeenReleasePort
import io.github.sophon.fightingnerd.inPort.SaveReleaseAsSeenUseCase

@ExcludeFromCoverage("plain port call")
internal class SaveReleaseAsSeenService(
    private val lastSeenReleasePort: LastSeenReleasePort,
): SaveReleaseAsSeenUseCase {
    override suspend fun invoke(version: String): EmptyResult<AppError> {
        return lastSeenReleasePort.saveLastSeenVersion(version)
    }
}
