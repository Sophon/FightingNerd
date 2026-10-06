package io.github.sophon.botdiscord.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.EwgfOperation
import io.github.sophon.discord.app.util.toEwgfOperation
import kotlin.test.Test

class EwgfOperationParserTest {
    @Test
    fun `blank query is data`() {
        // given
        val query = ""
        val expected = Result.Success(EwgfOperation.Data)

        // when
        val result = query.toEwgfOperation()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mention is search`() {
        // given
        val query = "<@786351781168939038>"
        val expected = Result.Success(EwgfOperation.Search("786351781168939038"))

        // when
        val result = query.toEwgfOperation()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `malformed mention is error`() {
        // given
        val query = "@111111111111"

        // when
        val result = query.toEwgfOperation()

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
    }
}
