package io.github.sophon.fightingnerd.feat.move.ui

import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.move.model.Property

internal data class UiMove(
    val move: Move,
    val propertySet: Set<Property> = emptySet(),
) {
    fun isExpandable(): Boolean {
        return move.notes.isNotEmpty() || move.urls.videoId.isNullOrEmpty().not()
    }

    companion object {
        fun Move.toUiMove(): UiMove {
            val result = UiMove(
                move = this,
                propertySet = buildSet {
                    invulnerability?.let { add(Property.Invincible) }
                    t8Properties?.let { props ->
                        if (props.isHeat) add(Property.Heat)
                        if (props.isHoming) add(Property.Homing)
                        if (props.isPowerCrush) add(Property.PowerCrush)
                        if (props.isHighCrush) add(Property.HighCrush)
                        if (props.isLowCrush) add(Property.LowCrush)
                    }
                    if (isThrow) { add(Property.Throw) }
                },
            )
            return result
        }
    }
}
