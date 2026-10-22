package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import kotlin.time.Instant

internal interface InstallationPort {
    suspend fun getInstallationTimestamp(): Result<Instant?, AppError>
    suspend fun saveInstallationTimestamp(timestamp: Instant): EmptyResult<AppError>
}
