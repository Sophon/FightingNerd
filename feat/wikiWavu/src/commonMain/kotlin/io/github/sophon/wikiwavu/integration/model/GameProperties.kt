package io.github.sophon.wikiwavu.integration.model

import io.github.sophon.core.wiki.model.MoveGameProperties
import kotlinx.serialization.Serializable

@Serializable
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
