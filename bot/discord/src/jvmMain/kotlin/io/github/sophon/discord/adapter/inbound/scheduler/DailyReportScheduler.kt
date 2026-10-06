package io.github.sophon.discord.adapter.inbound.scheduler

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.inPort.PostDailyReportUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

/**
 * Fires right after UTC midnight - the stats feature rolls the day over on first access, so the report
 * fetched then is the day that just ended.
 */
internal class DailyReportScheduler(
    private val scheduler: Scheduler,
    private val coroutineScope: CoroutineScope,
    private val postDailyReportUseCase: PostDailyReportUseCase,
) {
    fun start() {
        scheduler.start(
            initialDelay = untilNextUtcMidnight(),
            period = 24.hours,
            task = { postDailyReportUseCase() },
        )
            .onEach { result -> result.onError { error -> Napier.e(tag = TAG) { "Daily report failed: $error" } } }
            .launchIn(coroutineScope)
    }


    private fun untilNextUtcMidnight(): Duration {
        val now = Clock.System.now()
        val nextMidnight = Clock.System
            .todayIn(TimeZone.UTC)
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(TimeZone.UTC)
        val delay = (nextMidnight - now)
        return delay
    }


    private companion object {
        const val TAG = "DailyReportScheduler"
    }
}
