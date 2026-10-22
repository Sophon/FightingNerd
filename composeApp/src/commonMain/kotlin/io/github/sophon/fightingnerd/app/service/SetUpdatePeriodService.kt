package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import io.github.sophon.fightingnerd.app.outPort.UpdatePeriodPort
import io.github.sophon.fightingnerd.inPort.SetUpdatePeriodUseCase
import kotlin.time.Duration

internal class SetUpdatePeriodService(
    private val schedulerPort: SchedulerPort,
    private val updatePeriodPort: UpdatePeriodPort,
): SetUpdatePeriodUseCase {
    override suspend fun invoke(period: Duration?): EmptyResult<AppError> {
        val scheduleResult = if (period == null) {
            schedulerPort.cancel()
        } else {
            schedulerPort.schedule(period)
        }
        val result = scheduleResult.flatMap { updatePeriodPort.save(period) }
        return result
    }
}
