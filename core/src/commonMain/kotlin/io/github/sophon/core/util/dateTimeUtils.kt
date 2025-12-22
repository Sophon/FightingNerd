package io.github.sophon.core.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
fun Instant.toFormattedString(): String {
    return this
        .toLocalDateTime(TimeZone.UTC)
        .toString()
        .replace('T', ' ')
        .substringBefore('.')
}