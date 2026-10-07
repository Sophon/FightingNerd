package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import io.github.sophon.fightingnerd.inPort.SetUpdatePeriodUseCase
import kotlin.time.Duration

@ExcludeFromCoverage("plain port call")
internal class SetUpdatePeriodService(
    private val schedulerPort: SchedulerPort,
): SetUpdatePeriodUseCase {
    override suspend fun invoke(period: Duration?): EmptyResult<AppError> {
        val result = if (period == null) {
            schedulerPort.cancel()
        } else {
            schedulerPort.setPeriod(period)
        }
        return result
    }
}
