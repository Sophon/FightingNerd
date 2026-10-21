package io.github.sophon.wiki.model.game

import io.github.sophon.wiki.model.MoveGameProperties
import kotlinx.serialization.Serializable

/**
 * Properties every Wavu game shares - filters and groups that don't care about the game read these.
 */
sealed interface WavuMoveProperties : MoveGameProperties {
    val isHoming: Boolean
    val stance: String?
    val isHighCrush: Boolean
    val isLowCrush: Boolean
    val hasWallInteraction: Boolean
    val hasFloorInteraction: Boolean
}

@Serializable
data class T8Properties(
    val isHeat: Boolean = false,
    override val isHoming: Boolean = false,
    override val stance: String? = null,
    val isPowerCrush: Boolean = false,
    override val isHighCrush: Boolean = false,
    override val isLowCrush: Boolean = false,
    override val hasWallInteraction: Boolean = false,
    override val hasFloorInteraction: Boolean = false,
): WavuMoveProperties

@Serializable
data class Tag2Properties(
    override val isHoming: Boolean = false,
    override val stance: String? = null,
    override val isHighCrush: Boolean = false,
    override val isLowCrush: Boolean = false,
    override val hasWallInteraction: Boolean = false,
    override val hasFloorInteraction: Boolean = false,
): WavuMoveProperties
