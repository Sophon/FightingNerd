package io.github.sophon.cornerman.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.cornerman.screens.moveList.domain.getCategoryName
import kotlin.test.Test

class MoveMapperTest {
    //region getCategoryName() tests
    @Test
    fun `getCategoryName returns Heat for heat move`() {
        // given
        val move = createMove("H.2+3", properties = Move.T8Properties(isHeat = true))

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Heat")
    }

    @Test
    fun `getCategoryName returns DGF for DGF dot 1 input with stance property`() {
        // given
        val move = createMove("DGF.1", properties = Move.T8Properties(stance = "DGF"))

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("DGF")
    }

    @Test
    fun `getCategoryName returns FLE for FLE dot 3 plus 4 input with stance property`() {
        // given
        val move = createMove("FLE.3+4", properties = Move.T8Properties(stance = "FLE"))

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("FLE")
    }

    @Test
    fun `getCategoryName returns df for df plus 1 input`() {
        // given
        val move = createMove("df+1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("df")
    }

    @Test
    fun `getCategoryName returns f for f plus 2 input`() {
        // given
        val move = createMove("f+2")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("f")
    }

    @Test
    fun `getCategoryName returns Motion Input for qcf plus 1 input`() {
        // given
        val move = createMove("qcf+1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Motion Input")
    }

    @Test
    fun `getCategoryName returns Crouch for FC dot d plus 1 input`() {
        // given
        val move = createMove("FC.d+1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Crouch")
    }

    @Test
    fun `getCategoryName returns WS for ws1 input`() {
        // given
        val move = createMove("ws1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("WS")
    }

    @Test
    fun `getCategoryName returns BT Back Turned for BT dot 1 input`() {
        // given
        val move = createMove("BT.1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("BT (Back Turned)")
    }

    @Test
    fun `getCategoryName returns Throws for move with throw in notes`() {
        // given
        val move = createMove("1+3", notes = listOf("Homing", "Throw break 1 or 2"))

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Throws")
    }

    @Test
    fun `getCategoryName returns n for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("n")
    }

    @Test
    fun `getCategoryName returns n for neutral input 4`() {
        // given
        val move = createMove("4")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("n")
    }
    //endregion


    private fun createMove(
        input: String,
        notes: List<String> = emptyList(),
        properties: Move.T8Properties = Move.T8Properties()
    ) = Move(
        charName = "Charname",
        id = "test-${input}",
        name = "Test Move",
        input = input,
        damage = "10",
        startup = "i10",
        recovery = "r20",
        onBlock = "+0",
        onHit = "+5",
        onCH = null,
        notes = notes,
        aliases = listOf(),
        videoId = null,
        t8Properties = properties,
    )
}