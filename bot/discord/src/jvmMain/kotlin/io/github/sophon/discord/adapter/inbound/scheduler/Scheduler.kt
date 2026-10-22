package io.github.sophon.discord.adapter.inbound.scheduler

import io.github.sophon.discord.TIME_UPDATE_INTERVAL_H
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal class Scheduler {
    fun <T>start(
        initialDelay: Duration = Duration.ZERO,
        period: Duration = TIME_UPDATE_INTERVAL_H.hours,
        task: suspend () -> T,
    ): Flow<T> {
        return flow {
            delay(initialDelay)

            while (true) {
                emit(task.invoke())
                delay(period)
            }
        }
    }

    /**
     * Runs [task] shortly after every UTC midnight. The wait is recomputed from the wall clock each cycle,
     * because `delay` runs on the monotonic clock - a fixed 24 h period drifts, and a wall-clock correction
     * during the wait can wake it before midnight. See `docs/BUG-time_drift.md`.
     */
    fun <T>startDaily(task: suspend () -> T): Flow<T> {
        return flow {
            while (true) {
                delay(untilNextUtcMidnight() + MIDNIGHT_GRACE)
                emit(task.invoke())
            }
        }
    }
}


private val MIDNIGHT_GRACE = 1.minutes
