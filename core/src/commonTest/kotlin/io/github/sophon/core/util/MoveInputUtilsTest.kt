package io.github.sophon.core.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MoveInputUtilsTest {
    //region Slash
    @Test
    fun `aliases from slash handles 2x`() {
        // given
        val string = "j.5s1/j.2s1"
        val expected = listOf("j.5s1", "j.2s1")

        // when
        val result = string.createAliasesFromSlash(isPartial = false)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `aliases from slash handles gb`() {
        // given
        val string = "5l~m / 5m~l"
        val expected = listOf("5l~m", "5m~l")

        // when
        val result = string.createAliasesFromSlash(isPartial = false)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region
    @Test
    fun `use forward variant handles jump`() {
        // given
        val string = "j4/6ad"
        val expected = "j6ad"

        // when
        val result = string.useForwardVariantOnly()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `use forward variant handles normal slash`() {
        // given
        val string = "4/6a"
        val expected = "6a"

        // when
        val result = string.useForwardVariantOnly()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `use forward variant handles text close`() {
        // given
        val string = "c.4/6d"
        val expected = "c.6d"

        // when
        val result = string.useForwardVariantOnly()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `use forward variant handles keeps non back forward`() {
        // given
        val string = "214h/236d"
        val expected = "214h/236d"

        // when
        val result = string.useForwardVariantOnly()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}