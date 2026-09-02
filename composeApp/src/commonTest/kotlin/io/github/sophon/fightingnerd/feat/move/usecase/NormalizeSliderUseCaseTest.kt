package io.github.sophon.fightingnerd.feat.move.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MAX
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN
import kotlin.test.Test

internal class NormalizeSliderUseCaseTest {
    private val usecase = NormalizeSliderUseCase()

    @Test
    fun `min outside of MIN sets min to null`() {
        // given
        val minMaxBelowMin = MoveListState.FilterSheet.MinMax(
            min = (FRAME_MIN - 1),
            max = (FRAME_MAX - 2),
        )
        val expected = MoveListState.FilterSheet.MinMax(min = null, max = (FRAME_MAX - 2))

        // when
        val result = usecase(newMinMax = minMaxBelowMin)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `max outside of MAX sets max to null`() {
        // given
        val minMaxAboveMax = MoveListState.FilterSheet.MinMax(
            min = (FRAME_MIN + 1),
            max = (FRAME_MAX + 2),
        )
        val expected = MoveListState.FilterSheet.MinMax(min = (FRAME_MIN + 1), max = null)

        // when
        val result = usecase(newMinMax = minMaxAboveMax)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `minMax outside constants is set to null`() {
        // given
        val minMaxOutside = MoveListState.FilterSheet.MinMax(
            min = (FRAME_MIN - 1),
            max = (FRAME_MAX + 1),
        )
        val expected = null

        // when
        val result = usecase(newMinMax = minMaxOutside)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `invalid value returns null`() {
        // given
        val invalidMinMax = MoveListState.FilterSheet.MinMax(
            min = 5,
            max = 3,
        )
        val expected = null

        // when
        val result = usecase(newMinMax = invalidMinMax)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `null input returns null`() {
        // given
        val expected = null

        // when
        val result = usecase(newMinMax = null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `values within range are preserved`() {
        // given
        val minMaxWithinRange = MoveListState.FilterSheet.MinMax(
            min = (FRAME_MIN + 1),
            max = (FRAME_MAX - 1),
        )
        val expected = minMaxWithinRange

        // when
        val result = usecase(newMinMax = minMaxWithinRange)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `boundary values are preserved`() {
        // given
        val minMaxAtBoundaries = MoveListState.FilterSheet.MinMax(
            min = FRAME_MIN,
            max = FRAME_MAX,
        )
        val expected = minMaxAtBoundaries

        // when
        val result = usecase(newMinMax = minMaxAtBoundaries)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `both min and max null returns null`() {
        // given
        val bothNull = MoveListState.FilterSheet.MinMax(
            min = null,
            max = null,
        )
        val expected = null

        // when
        val result = usecase(newMinMax = bothNull)

        //then
        assertThat(result).isEqualTo(expected)
    }
}
