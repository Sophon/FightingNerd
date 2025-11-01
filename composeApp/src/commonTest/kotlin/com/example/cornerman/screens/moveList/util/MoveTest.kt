package com.example.cornerman.screens.moveList.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.wikiwavu.domain.model.Move
import kotlin.test.Test

class MoveTest {
    @Test
    fun `cleanComboLinks removes wiki link from onHit`() {
        // given
        val move = createMove(onHit = "[[Dragunov combos#Mini-combos|+22a]]")

        // when
        val result = move.cleanComboLinks()

        // then
        assertThat(result.onHit).isEqualTo("+22a")
    }

    @Test
    fun `cleanComboLinks removes wiki link from onCH`() {
        // given
        val move = createMove(onCH = "[[Dragunov combos#Mini-combos|+52a]]")

        // when
        val result = move.cleanComboLinks()

        // then
        assertThat(result.onCH).isEqualTo("+52a")
    }

    @Test
    fun `cleanComboLinks preserves normal onHit value`() {
        // given
        val move = createMove(onHit = "+5")

        // when
        val result = move.cleanComboLinks()

        // then
        assertThat(result.onHit).isEqualTo("+5")
    }

    @Test
    fun `cleanComboLinks preserves normal onCH value`() {
        // given
        val move = createMove(onCH = "+8")

        // when
        val result = move.cleanComboLinks()

        // then
        assertThat(result.onCH).isEqualTo("+8")
    }

    @Test
    fun `cleanComboLinks handles null onHit`() {
        // given
        val move = createMove(onHit = null)

        // when
        val result = move.cleanComboLinks()

        // then
        assertThat(result.onHit).isEqualTo(null)
    }

    @Test
    fun `cleanComboLinks handles partial wiki syntax`() {
        // given
        val move = createMove(onHit = "[[incomplete")

        // when
        val result = move.cleanComboLinks()

        // then
        assertThat(result.onHit).isEqualTo("[[incomplete")
    }

    @Test
    fun `cleanComboLinks handles complex value with parentheses`() {
        // given
        val move = createMove(onHit = "[[Page|+22a (+12)]]")

        // when
        val result = move.cleanComboLinks()

        // then
        assertThat(result.onHit).isEqualTo("+22a (+12)")
    }

    private fun createMove(
        onHit: String? = null,
        onCH: String? = null,
    ) = Move(
        charName = "Dragunov",
        id = "test",
        input = "1",
        onHit = onHit,
        onCH = onCH,
    )
}