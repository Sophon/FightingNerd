package io.github.sophon.core.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MoveInputUtilsTest {
    //region Slash
    @Test
    fun `alias handles full slash`() {
        // given
        val input = "j5s1/j2s1"
        val expectedAlias = listOf("j5s1", "j2s1")

        // when
        val resultAlias =  input.create2dAliases(isPartial = false)

        //then
        assertThat(resultAlias).isEqualTo(expectedAlias)
    }

    @Test
    fun `alias handles plink`() {
        // given
        val input = "5l~m / 5m~l"
        val expectedAlias = listOf("5l~m", "5m~l")

        // when
        val resultAlias = input.create2dAliases(isPartial = false)

        //then
        assertThat(resultAlias).isEqualTo(expectedAlias)
    }

    @Test
    fun `alias handles jump`() {
        // given
        val input = "j4/6ad"
        val expectedAlias = listOf("j4ad", "j6ad")

        // when
        val resultAlias =  input.create2dAliases(isPartial = true)

        //then
        assertThat(resultAlias).isEqualTo(expectedAlias)
    }

    @Test
    fun `alias handles partial slash`() {
        // given
        val input = "4/6a"
        val expectedAlias = listOf("4a", "6a")

        // when
        val resultAlias = input.create2dAliases(isPartial = true)

        //then
        assertThat(resultAlias).isEqualTo(expectedAlias)
    }

    @Test
    fun `alias handles slash with close`() {
        // given
        val input = "c4/6d"
        val expectedAlias = listOf("c4d", "c6d")

        // when
        val result = input.create2dAliases(isPartial = true)

        //then
        assertThat(result).isEqualTo(expectedAlias)
    }
    //endregion

    //region Normalize
    @Test
    fun `normalize 2d handles close`() {
        // given
        val input = "(close)6c"
        val expected = "c6c"

        // when
        val result = input.normalize2dInputs()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}