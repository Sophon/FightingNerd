package io.github.sophon.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun today(): LocalDate {
    return Clock.System.todayIn(TimeZone.UTC)
}
