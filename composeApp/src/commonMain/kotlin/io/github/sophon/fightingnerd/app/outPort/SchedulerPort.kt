package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

internal interface SchedulerPort {
    suspend fun setPeriod(duration: Duration): EmptyResult<AppError>
    suspend fun cancel(): EmptyResult<AppError>
    fun subscribeToPeriod(): Flow<Duration?>
}
