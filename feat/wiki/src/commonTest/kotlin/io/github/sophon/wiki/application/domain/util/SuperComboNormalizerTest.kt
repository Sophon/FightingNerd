package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.SF6MoveProperties
import kotlin.test.Test

class SuperComboNormalizerTest {

    //region input
    @Test
    fun `input from the move ID is lowercased`() {
        //given
        val move = SuperComboMoveSource.aangThrow
        val expected = "a+d"

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `version suffix from the move ID survives normalization`() {
        //given
        val move = SuperComboMoveSource.ryuuenbuFlame
        val expected = "214hp_flame"

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.input).isEqualTo(expected)
    }
    //endregion

    //region aliases
    @Test
    fun `motion notation gets its motion alias`() {
        //given
        val move = SuperComboMoveSource.hadoken
        val expected = listOf("qcfhp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `crouching notation gets its cr alias`() {
        //given
        val move = SuperComboMoveSource.crHP
        val expected = listOf("crhp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `spd notation drops the plus and gets its spd alias`() {
        //given
        val move = SuperComboMoveSource.screwPiledriver
        val expected = listOf("spdhp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `or notation stays an alias and splits into aliases`() {
        //given
        val move = SuperComboMoveSource.swiftThrust
        val expected = listOf("4/6mp", "4mp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `wiki notation that differs from the input stays an alias`() {
        //given
        val move = SuperComboMoveSource.nightshadeChaser
        val expected = "214lp~6p"

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).contains(expected)
    }

    @Test
    fun `super gets its level from the meter cost`() {
        //given
        val move = SuperComboMoveSource.dragonlashFlame
        val expected = listOf("sa1")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `critical art gets no super level`() {
        //given
        val move = SuperComboMoveSource.shinryuReppaCa
        val expected = listOf("236236p")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion
}

/**
 * Moves as the SuperCombo adapter maps them - the input from the move ID, the wiki notation as the only alias.
 */
private object SuperComboMoveSource {
    val hadoken = superComboMove(remoteId = "ken_236hp", input = "236hp", wikiInput = "236HP", type = "special", superGain = "600 (420)")
    val crHP = superComboMove(remoteId = "ken_2hp", input = "2hp", wikiInput = "2HP", type = "ground_normal", superGain = "1000 (700)")
    val dragonlashFlame = superComboMove(remoteId = "ken_214214k", input = "214214k", wikiInput = "214214K", type = "super", superGain = "-10000")
    val shinryuReppaCa = superComboMove(remoteId = "ken_236236p(ca)", input = "236236p(ca)", wikiInput = "236236P", type = "super", superGain = "-30000")
    val screwPiledriver = superComboMove(remoteId = "zangief_360hp", input = "360hp", wikiInput = "360+HP", type = "special", superGain = "4000 (2800)")
    val swiftThrust = superComboMove(remoteId = "Chun-Li_6mp", input = "6mp", wikiInput = "4/6MP", type = "ground_normal", superGain = "500 (350)")
    val nightshadeChaser = superComboMove(remoteId = "a.k.i._214lp_6p", input = "214lp_6p", wikiInput = "214LP~6P", type = "special", superGain = "600 (420)")
    val ryuuenbuFlame = superComboMove(remoteId = "mai_214hp_flame", input = "214hp_flame", wikiInput = "214HP", type = "special", superGain = "600 (420)")
    val aangThrow = superComboMove(remoteId = "Aang_A+D", input = "A+D", wikiInput = "A+D", type = "throw", superGain = "0")
}

private fun superComboMove(
    remoteId: String,
    input: String,
    wikiInput: String,
    type: String,
    superGain: String,
): Move {
    val move = Move(
        input = input,
        remoteId = remoteId,
        aliases = listOf(wikiInput),
        type = type,
        urls = Move.Urls(wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6"),
        gameProperties = SF6MoveProperties(superGainOnHit = superGain),
    )
    return move
}
