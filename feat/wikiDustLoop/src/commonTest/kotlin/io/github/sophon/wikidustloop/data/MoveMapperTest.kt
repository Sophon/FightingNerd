package io.github.sophon.wikidustloop.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MoveMapperTest {
    //region Aliases
    @Test
    fun `formAliases handles regular input`() {
        //given
        val string = "c.m"
        val expected = listOf<String>()

        //when
        val result = string.formAliases()

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
        val result1 = string1.formAliases()
        val result2 = string2.formAliases()

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
        val result = string.formAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}