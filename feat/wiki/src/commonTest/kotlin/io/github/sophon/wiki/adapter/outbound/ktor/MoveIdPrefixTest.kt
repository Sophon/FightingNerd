package io.github.sophon.wiki.adapter.outbound.ktor

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MoveIdPrefixTest {

    //region find
    @Test
    fun `prefix is the character part shared by the move IDs`() {
        //given
        val moveIdList = listOf("mai_214hp", "mai_214hp_flame", "mai_5lp")
        val expected = "mai_"

        //when
        val result = moveIdList.findMoveIdPrefix()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `multi-word character part is one prefix`() {
        //given
        val moveIdList = listOf("general_shao_d2", "general_shao_uad2", "general_shao_b2")
        val expected = "general_shao_"

        //when
        val result = moveIdList.findMoveIdPrefix()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `prefix with dots is found`() {
        //given
        val moveIdList = listOf("a.k.i._214lp_6p", "a.k.i._214lp_6p_burst", "a.k.i._5lp")
        val expected = "a.k.i._"

        //when
        val result = moveIdList.findMoveIdPrefix()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `prefix is found regardless of case`() {
        //given
        val moveIdList = listOf("Jamie_236hp_dl2", "Jamie_5lp", "jamie_mpmk_66_drc")
        val expected = "jamie_"

        //when
        val result = moveIdList.findMoveIdPrefix()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `prefix shared by most move IDs wins over the rest`() {
        //given
        val moveIdList = listOf("LE_214HP", "LE_236HP", "LE_5LP", "tenraiha_anvil")
        val expected = "le_"

        //when
        val result = moveIdList.findMoveIdPrefix()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `prefix with a digit belongs to the move`() {
        //given
        val moveIdList = listOf("ak_5b_close", "ak_5b_far")
        val expected = "ak_"

        //when
        val result = moveIdList.findMoveIdPrefix()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `single move ID has no prefix`() {
        //given
        val moveIdList = listOf("ken_236hp")
        val expected = ""

        //when
        val result = moveIdList.findMoveIdPrefix()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region remove
    @Test
    fun `prefix is removed regardless of case`() {
        //given
        val moveId = "Jamie_236hp_dl2"
        val expected = "236hp_dl2"

        //when
        val result = moveId.removeMoveIdPrefix("jamie_")

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move ID without the prefix stays whole`() {
        //given
        val moveId = "tenraiha_anvil"
        val expected = "tenraiha_anvil"

        //when
        val result = moveId.removeMoveIdPrefix("le_")

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move ID that is only the prefix stays whole`() {
        //given
        val moveId = "Aang_"
        val expected = "Aang_"

        //when
        val result = moveId.removeMoveIdPrefix("aang_")

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}
