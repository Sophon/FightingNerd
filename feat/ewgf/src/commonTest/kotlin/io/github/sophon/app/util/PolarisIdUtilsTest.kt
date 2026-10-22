package io.github.sophon.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class PolarisIdUtilsTest {
    @Test
    fun `dashes are removed`() {
        // given
        val tekkenId = "2Edf-6Arh-Mm3J"
        val expected = "2Edf6ArhMm3J"

        // when
        val result = tekkenId.toPolarisId()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `id without dashes is unchanged`() {
        // given
        val tekkenId = "2Edf6ArhMm3J"
        val expected = "2Edf6ArhMm3J"

        // when
        val result = tekkenId.toPolarisId()

        // then
        assertThat(result).isEqualTo(expected)
    }
}
