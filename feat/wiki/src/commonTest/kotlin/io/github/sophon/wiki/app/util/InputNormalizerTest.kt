package io.github.sophon.wiki.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.app.util.create2dAliases
import io.github.sophon.wiki.app.util.normalize2dInputs
import kotlin.test.Test

class InputNormalizerTest {

    //region normalize2dInputs
    @Test
    fun `dotted close prefix becomes cl`() {
        //given
        val input = "c.D"
        val expected = "cld"

        //when
        val result = input.normalize2dInputs()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `worded close prefix becomes cl`() {
        //given
        val input = "(close) 4/6C"
        val expected = "cl4/6c"

        //when
        val result = input.normalize2dInputs()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `button pair without a dot is not a close input`() {
        //given
        val input = "CD"
        val expected = "cd"

        //when
        val result = input.normalize2dInputs()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region create2dAliases
    @Test
    fun `close input is also found by the other close spellings`() {
        //given
        val input = "cls"
        val expected = listOf("cl.s", "c.s", "cs")

        //when
        val result = input.create2dAliases(isPartial = false)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `input starting with c but not cl gets no close aliases`() {
        //given
        val input = "cd"
        val expected = emptyList<String>()

        //when
        val result = input.create2dAliases(isPartial = false)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `jump input gets its dotted spelling`() {
        //given
        val input = "j2k"
        val expected = listOf("j.2k")

        //when
        val result = input.create2dAliases(isPartial = false)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}
