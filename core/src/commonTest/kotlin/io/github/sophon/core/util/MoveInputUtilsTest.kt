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
        val expected = listOf(
            "j.5s1",
            "j.2s1",
        )

        // when
        val result = string.createAliasesFromSlash(isPartial = false)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `aliases from slash handles mizuumi`() {
        // given
        val string = "j4/6ad"
        val expected = listOf("j4", "6ad")

        // when
        val result = string.createAliasesFromSlash(isPartial = false)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}