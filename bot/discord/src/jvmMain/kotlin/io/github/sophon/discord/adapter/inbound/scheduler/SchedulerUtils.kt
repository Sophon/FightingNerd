package io.github.sophon.discord.adapter.inbound.scheduler

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

internal fun untilNextUtcMidnight(now: Instant = Clock.System.now()): Duration {
    val nextMidnight = now
        .toLocalDateTime(TimeZone.UTC)
        .date
        .plus(1, DateTimeUnit.DAY)
        .atStartOfDayIn(TimeZone.UTC)
    val delay = (nextMidnight - now)
    return delay
}
