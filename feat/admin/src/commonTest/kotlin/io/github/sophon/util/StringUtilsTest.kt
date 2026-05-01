package io.github.sophon.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.integration.model.Source
import kotlin.test.Test

class StringUtilsTest {
    //region toSourceAndMessage
    @Test
    fun `toSourceAndMessage handles standard query`() {
        //given
        val string = "tham8778-786351781168939038-717398042562658347 test"
        val expected = Pair(
            Source(
                username = "tham8778",
                id = "786351781168939038",
                channelId = "717398042562658347",
            ),
            "test",
        )

        //when
        val result = string.toSourceAndMessage()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toSourceAndMessage handles empty string`() {
        //given
        val string = ""
        val expected = null

        //when
        val result = string.toSourceAndMessage()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toSourceAndMessage handles incomplete string`() {
        //given
        val string = "tham8778-786351781168939038"
        val expected = null

        //when
        val result = string.toSourceAndMessage()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toSourceAndMessage handles no message`() {
        //given
        val string = "tham8778-786351781168939038-717398042562658347"
        val expected = null

        //when
        val result = string.toSourceAndMessage()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toSource
    @Test
    fun `toSource handles standard string`() {
        //given
        val string = "tham8778-786351781168939038-717398042562658347"
        val expected = Source(
            username = "tham8778",
            id = "786351781168939038",
            channelId = "717398042562658347",
        )

        //when
        val result = string.toSource()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toSource handles incomplete string`() {
        //given
        val string = "tham8778-786351781168939038"
        val expected = null

        //when
        val result = string.toSource()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}