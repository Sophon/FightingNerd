package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MAX
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN

internal class NormalizeSliderUseCase {
    operator fun invoke(
        newMinMax: MoveListState.FilterSheet.MinMax?,
        sliderMin: Int = FRAME_MIN,
        sliderMax: Int = FRAME_MAX,
    ): MoveListState.FilterSheet.MinMax? {
        if (newMinMax == null) return null
        if (newMinMax.isValid.not()) return null

        val sliderMin = sliderMin - 1
        val newMin = if (newMinMax.min != null && newMinMax.min <= sliderMin) {
            null
        } else {
            newMinMax.min
        }

        val sliderMax = sliderMax + 1
        val newMax = if (newMinMax.max != null && newMinMax.max >= sliderMax) {
            null
        } else {
            newMinMax.max
        }

        val normalized = if (newMin == null && newMax == null) {
            null
        } else {
            newMinMax.copy(min = newMin, max = newMax)
        }

        return normalized
    }
}
