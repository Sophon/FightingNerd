package io.github.sophon.app.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

internal fun Clock.todayUtc(): LocalDate {
    return todayIn(TimeZone.UTC)
}
