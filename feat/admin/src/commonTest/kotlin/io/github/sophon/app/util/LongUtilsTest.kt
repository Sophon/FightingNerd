package io.github.sophon.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class LongUtilsTest {
    @Test
    fun `true is 1`() {
        assertThat(true.toLong()).isEqualTo(1L)
    }

    @Test
    fun `false is 0`() {
        assertThat(false.toLong()).isEqualTo(0L)
    }

    @Test
    fun `0 is false`() {
        assertThat(0L.toBoolean()).isEqualTo(false)
    }

    @Test
    fun `1 is true`() {
        assertThat(1L.toBoolean()).isEqualTo(true)
    }
}
