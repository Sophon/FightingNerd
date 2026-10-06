package io.github.sophon.discord.adapter.inbound.scheduler

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration

internal fun untilNextUtcMidnight(): Duration {
    val now = Clock.System.now()
    val nextMidnight = Clock.System
        .todayIn(TimeZone.UTC)
        .plus(1, DateTimeUnit.DAY)
        .atStartOfDayIn(TimeZone.UTC)
    val delay = (nextMidnight - now)
    return delay
}
