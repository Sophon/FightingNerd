package io.github.sophon.domain.model

import kotlinx.datetime.LocalDateTime

data class BattleSet(
    val battleList: List<Battle>,
    val player: Combatant,
    val opponent: Combatant,
    val score: Score,
    val battleType: BattleType,
    val date: LocalDateTime,
    val version: Int,
    val stageId: Int,
)
