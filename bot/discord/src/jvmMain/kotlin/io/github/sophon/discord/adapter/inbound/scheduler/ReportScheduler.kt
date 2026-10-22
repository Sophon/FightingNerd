package io.github.sophon.discord.adapter.inbound.scheduler

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.discord.inPort.PostDailyReportUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class ReportScheduler(
    private val scheduler: Scheduler,
    private val coroutineScope: CoroutineScope,
    private val postDailyReportUseCase: PostDailyReportUseCase,
) {
    fun start() {
        scheduler.startDaily { postDailyReportUseCase() }
            .onEach { result -> result.onError { error -> Napier.e(tag = TAG) { "Daily report failed: $error" } } }
            .launchIn(coroutineScope)
    }


    private companion object {
        const val TAG = "DailyReportScheduler"
    }
}
