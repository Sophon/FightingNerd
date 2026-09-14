package io.github.sophon.fightingnerd.feat.review

import kotlin.time.Duration

internal sealed interface SessionContext {
    val duration: Duration

    data class MoveList(override val duration: Duration = Duration.ZERO) : SessionContext

    data class About(override val duration: Duration = Duration.ZERO) : SessionContext

    data class Quiz(
        override val duration: Duration = Duration.ZERO,
        val correctAnswerPct: Int,
    ): SessionContext

    object Donation : SessionContext {
        override val duration = Duration.INFINITE
    }
}
