package io.github.sophon.integration.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Battle(
    val player: Combatant,
    val opponent: Combatant,
    val score: Score,
    val battleType: BattleType,
    val date: LocalDateTime,
    val version: Int,
    val stageId: Int,
)
