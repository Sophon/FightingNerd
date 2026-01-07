package io.github.sophon.wikidustloop.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.feature.Game
import kotlin.test.Test

class MoveMapperTest {
    val gb = Game.GBVSR.id
    val gg = Game.GGST.id

    //region Aliases
    @Test
    fun `formAliases handles regular input`() {
        //given
        val string = "c.m"
        val expected = listOf<String>()

        //when
        val result = string.formAliases(gb)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles form input`() {
        //given
        val string1 = "f.l[k]"
        val string2 = "c.m[ex]"
        val expected1 = listOf("k.f.l")
        val expected2 = listOf("ex.c.m")

        //when
        val result1 = string1.formAliases(gb)
        val result2 = string2.formAliases(gb)

        //then
        assertThat(result1).isEqualTo(expected1)
        assertThat(result2).isEqualTo(expected2)
    }

    @Test
    fun `formAliases handles empty form input`() {
        //given
        val string = "f.l[]"
        val expected = listOf<String>()

        //when
        val result = string.formAliases(gb)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles slash inputs`() {
        // given
        val string = "236s/h/d"
        val expected = listOf("236s", "236h", "236d")

        // when
        val result = string.formAliases(gg)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}