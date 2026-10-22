package io.github.sophon.discord.adapter.inbound.scheduler

import io.github.sophon.discord.inPort.RefreshGlossaryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlin.time.Duration.Companion.days

internal class GlossaryScheduler(
    private val scheduler: Scheduler,
    private val coroutineScope: CoroutineScope,
    private val refreshGlossaryUseCase: RefreshGlossaryUseCase,
) {
    fun start() {
        scheduler.start(
            initialDelay = untilNextUtcMidnight(),
            period = 7.days,
            task = { refreshGlossaryUseCase() },
        )
            .launchIn(coroutineScope)
    }
}
