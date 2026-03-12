package io.github.sophon.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Score(
    val player: Int,
    val opponent: Int,
) {
    val outcome: Outcome
        get() = when {
            player > opponent -> Outcome.WIN
            player < opponent -> Outcome.LOSE
            else -> Outcome.DRAW
        }

    enum class Outcome {
        WIN,
        LOSE,
        DRAW,
    }
}
