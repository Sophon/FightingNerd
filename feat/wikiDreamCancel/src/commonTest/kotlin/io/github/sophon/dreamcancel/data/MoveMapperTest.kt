package io.github.sophon.dreamcancel.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MoveMapperTest {
    //region input decoding
    @Test
    fun `use forward variant of move only`() {
        //given
        val inputs = listOf(
            "4/6ac > d",
            "5/6/2+bc",
            "cl.d",
            "2b",
        )
        val expected = listOf(
            "6ac > d",
            "6+bc",
            "cl.d",
            "2b",
        )

        //when
        val result = inputs.map { it.useForwardVariantOnly() }

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}