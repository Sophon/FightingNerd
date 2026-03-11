package io.github.sophon.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Battle(
    val player: Combatant,
    val opponent: Combatant,
    val score: Score,
    val type: Type,
    val date: LocalDateTime,
    val version: Int,
    val stageId: Int,
) {
    enum class Type {
        QUICK,
        RANKED,
        LOBBY,
    }

    @Serializable
    data class Combatant(
        val name: String,
        val polarisId: String,
        val character: String,
        val rank: String,
        val prowess: Int,
        val region: Region,
    )

    enum class Region {
        ASIA,
        MIDDLE_EAST,
        OCEANIA,
        AMERICAS,
        EUROPE,
    }

    @Serializable
    data class Score(
        val playerRounds: Int,
        val opponentRounds: Int,
    ) {
        val outcome: Outcome
            get() = when {
                playerRounds > opponentRounds -> Outcome.WIN
                playerRounds < opponentRounds -> Outcome.LOSE
                else -> Outcome.DRAW
            }

        enum class Outcome {
            WIN,
            LOSE,
            DRAW,
        }
    }
}
