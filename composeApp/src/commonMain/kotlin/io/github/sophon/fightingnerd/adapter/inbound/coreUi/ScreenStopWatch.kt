package io.github.sophon.fightingnerd.adapter.inbound.coreUi

import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class ScreenStopWatch(
    timeSource: TimeSource = TimeSource.Monotonic,
) {
    private val start: TimeMark = timeSource.markNow()


    fun elapsed(): Duration {
        val elapsed = start.elapsedNow()
        return elapsed
    }
}
