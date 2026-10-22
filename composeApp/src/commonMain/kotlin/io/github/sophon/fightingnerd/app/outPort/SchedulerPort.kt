package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import kotlin.time.Duration

internal interface SchedulerPort {
    suspend fun schedule(period: Duration): EmptyResult<AppError>
    suspend fun cancel(): EmptyResult<AppError>
}
