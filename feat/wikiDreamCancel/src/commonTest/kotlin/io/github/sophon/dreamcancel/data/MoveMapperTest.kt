package io.github.sophon.dreamcancel.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.util.add2dAliases
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.useForwardVariantOnly
import kotlin.test.Test

class MoveMapperTest {
    @Test
    fun `input and alias test`() {
        // given
        val input = "(close) 4/6C"
        val expectedInput = "c.6c"
        val expectedAliases = listOf("c6c")

        // when
        val resultInput = input
            .orDash()
            .decodeHtmlEntities()
            .normalize2dInputs()
            .useForwardVariantOnly()
            .lowercase()
        val resultAliases = resultInput.add2dAliases()

        //then
        assertThat(resultInput).isEqualTo(expectedInput)
        assertThat(resultAliases).isEqualTo(expectedAliases)
    }
}