package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.InstallationPort
import io.github.sophon.fightingnerd.inPort.RecordInstallationUseCase
import kotlin.time.Clock

internal class RecordInstallationService(
    private val installationPort: InstallationPort,
): RecordInstallationUseCase {
    override suspend fun invoke(): EmptyResult<AppError> {
        val result = installationPort.getInstallationTimestamp().flatMap { timestamp ->
            if (timestamp == null) {
                installationPort.saveInstallationTimestamp(Clock.System.now())
            } else {
                Result.Success(Unit)
            }
        }
        return result
    }
}
