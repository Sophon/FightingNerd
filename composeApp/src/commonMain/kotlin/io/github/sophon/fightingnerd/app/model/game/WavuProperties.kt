package io.github.sophon.fightingnerd.app.model.game

import io.github.sophon.fightingnerd.app.model.MoveGameProperties

data class T8Properties(
    val isHeat: Boolean = false,
    val isHoming: Boolean = false,
    val stance: String? = null,
    val isPowerCrush: Boolean = false,
    val isHighCrush: Boolean = false,
    val isLowCrush: Boolean = false,
    val hasWallInteraction: Boolean = false,
    val hasFloorInteraction: Boolean = false,
): MoveGameProperties
