package io.github.sophon.botdiscord.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.discord.app.util.removeTag
import kotlin.test.Test

class StringTest {
    @Test
    fun `tag is removed up to the first space`() {
        // given
        val content = "<@1438716136790429776> fd jin df+1"
        val expected = "fd jin df+1"

        // when
        val result = content.removeTag()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `text without a tag stays as is`() {
        // given
        val content = "fd jin df+1"

        // when
        val result = content.removeTag()

        // then
        assertThat(result).isEqualTo(content)
    }
}
