package io.github.sophon.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Instant

class DateTimeUtilsTest {
    @Test
    fun `last second of the day in UTC is still that day`() {
        // given
        val expected = LocalDate(2026, 10, 6)
        val clock = FixedClock(Instant.parse("2026-10-06T23:59:59Z"))

        // when
        val result = clock.todayUtc()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `midnight in UTC is the next day`() {
        // given
        val expected = LocalDate(2026, 10, 7)
        val clock = FixedClock(Instant.parse("2026-10-07T00:00:00Z"))

        // when
        val result = clock.todayUtc()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FixedClock(private val instant: Instant): Clock {
        override fun now(): Instant = instant
    }
}
