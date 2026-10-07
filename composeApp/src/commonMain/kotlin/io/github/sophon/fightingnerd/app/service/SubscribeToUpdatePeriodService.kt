package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import io.github.sophon.fightingnerd.inPort.SubscribeToUpdatePeriodUseCase
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

@ExcludeFromCoverage("plain port call")
internal class SubscribeToUpdatePeriodService(
    private val schedulerPort: SchedulerPort,
): SubscribeToUpdatePeriodUseCase {
    override fun invoke(): Flow<Duration?> {
        return schedulerPort.subscribeToPeriod()
    }
}
