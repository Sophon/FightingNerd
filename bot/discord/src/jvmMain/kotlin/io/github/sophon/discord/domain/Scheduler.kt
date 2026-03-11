package io.github.sophon.discord.domain

import io.github.sophon.discord.TIME_UPDATE_INTERVAL_H
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class Scheduler {
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
}