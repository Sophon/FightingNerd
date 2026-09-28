package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.SF6MoveProperties
import kotlin.test.Test

class SuperComboNormalizerTest {

    //region input
    @Test
    fun `spd input drops the plus`() {
        //given
        val move = SuperComboMoveSource.screwPiledriver
        val expected = "360hp"

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `or input keeps its slash`() {
        //given
        val move = SuperComboMoveSource.swiftThrust
        val expected = "4/6mp"

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.input).isEqualTo(expected)
    }
    //endregion

    //region aliases
    @Test
    fun `motion input gets its motion alias`() {
        //given
        val move = SuperComboMoveSource.hadoken
        val expected = listOf("qcfhp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `crouching input gets its cr alias`() {
        //given
        val move = SuperComboMoveSource.crHP
        val expected = listOf("crhp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `spd input gets its spd alias`() {
        //given
        val move = SuperComboMoveSource.screwPiledriver
        val expected = listOf("spdhp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `or input splits into aliases`() {
        //given
        val move = SuperComboMoveSource.swiftThrust
        val expected = listOf("4mp", "6mp")

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
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
        val move = SuperComboMoveSource.shinryuReppa
        val expected = emptyList<String>()

        //when
        val result = move.normalizeSuperCombo()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion
}

/**
 * Moves as the SuperCombo adapter maps them - wiki notation, no aliases yet.
 */
private object SuperComboMoveSource {
    val hadoken = superComboMove(characterId = "ken", id = "ken_236hp", input = "236HP", type = "special", superGain = "600 (420)")
    val crHP = superComboMove(characterId = "ken", id = "ken_2hp", input = "2HP", type = "ground_normal", superGain = "1000 (700)")
    val dragonlashFlame = superComboMove(characterId = "ken", id = "ken_214214k", input = "214214K", type = "super", superGain = "-10000")
    val shinryuReppa = superComboMove(characterId = "ken", id = "ken_236236p(ca)", input = "236236P", type = "super", superGain = "-30000")
    val screwPiledriver = superComboMove(characterId = "zangief", id = "zangief_360hp", input = "360+HP", type = "special", superGain = "4000 (2800)")
    val swiftThrust = superComboMove(characterId = "chun_li", id = "Chun-Li_6mp", input = "4/6MP", type = "ground_normal", superGain = "500 (350)")
}

private fun superComboMove(
    characterId: String,
    id: String,
    input: String,
    type: String,
    superGain: String,
): Move {
    val move = Move(
        characterId = characterId,
        id = id,
        input = input,
        type = type,
        urls = Move.Urls(wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6"),
        gameProperties = SF6MoveProperties(superGainOnHit = superGain),
    )
    return move
}
