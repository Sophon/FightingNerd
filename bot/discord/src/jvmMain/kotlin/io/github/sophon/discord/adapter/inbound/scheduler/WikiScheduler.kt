package io.github.sophon.discord.adapter.inbound.scheduler

import io.github.sophon.discord.inPort.RefreshWikiUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlin.time.Duration.Companion.hours

internal class WikiScheduler(
    private val scheduler: Scheduler,
    private val coroutineScope: CoroutineScope,
    private val refreshWikiUseCase: RefreshWikiUseCase,
) {
    fun start() {
        scheduler.start(
            initialDelay = untilNextUtcMidnight(),
            period = 24.hours,
            task = { refreshWikiUseCase() },
        )
            .launchIn(coroutineScope)
    }
}
