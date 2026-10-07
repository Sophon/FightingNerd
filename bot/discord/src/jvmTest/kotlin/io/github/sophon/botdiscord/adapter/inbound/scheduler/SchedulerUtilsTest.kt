package io.github.sophon.botdiscord.adapter.inbound.scheduler

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.discord.adapter.inbound.scheduler.untilNextUtcMidnight
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class SchedulerUtilsTest {
    @Test
    fun `one second before midnight waits one second`() {
        // given
        val expected = 1.seconds
        val now = Instant.parse("2026-10-06T23:59:59Z")

        // when
        val result = untilNextUtcMidnight(now)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `exactly midnight waits a full day`() {
        // given
        val expected = 24.hours
        val now = Instant.parse("2026-10-07T00:00:00Z")

        // when
        val result = untilNextUtcMidnight(now)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `run after the grace period waits until the next midnight`() {
        // given
        val expected = (23.hours + 59.minutes)
        val now = Instant.parse("2026-10-07T00:01:00Z")

        // when
        val result = untilNextUtcMidnight(now)

        // then
        assertThat(result).isEqualTo(expected)
    }
}
