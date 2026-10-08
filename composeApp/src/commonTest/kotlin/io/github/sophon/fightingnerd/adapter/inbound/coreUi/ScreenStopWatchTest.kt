package io.github.sophon.fightingnerd.adapter.inbound.coreUi

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

internal class ScreenStopWatchTest {
    @Test
    fun `elapsed time is measured from creation`() {
        // given
        val timeSource = TestTimeSource()
        timeSource += 5.seconds
        val stopWatch = ScreenStopWatch(timeSource)
        timeSource += 1_250.milliseconds
        val expected = 1_250.milliseconds

        // when
        val result = stopWatch.elapsed()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `elapsed time keeps growing`() {
        // given
        val timeSource = TestTimeSource()
        val stopWatch = ScreenStopWatch(timeSource)
        timeSource += 2.seconds
        stopWatch.elapsed()
        timeSource += 3.seconds
        val expected = 5.seconds

        // when
        val result = stopWatch.elapsed()

        // then
        assertThat(result).isEqualTo(expected)
    }
}
