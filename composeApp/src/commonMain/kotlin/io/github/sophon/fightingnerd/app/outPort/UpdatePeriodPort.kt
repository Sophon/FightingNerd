package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

internal interface UpdatePeriodPort {
    fun subscribe(): Flow<Duration?>
    suspend fun save(period: Duration?): EmptyResult<AppError>
}
