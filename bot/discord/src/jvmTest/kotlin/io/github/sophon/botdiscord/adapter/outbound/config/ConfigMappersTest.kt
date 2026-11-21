package io.github.sophon.botdiscord.adapter.outbound.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.DataError
import io.github.sophon.discord.adapter.outbound.config.toDomainError
import kotlin.test.Test

class ConfigMappersTest {
    @Test
    fun `remote error becomes download error carrying its name`() {
        // given
        val error = DataError.Remote.NO_INTERNET
        val expected = "DownloadError(NO_INTERNET)"

        // when
        val result = error.toDomainError()

        // then
        assertThat(result.toString()).isEqualTo(expected)
    }
}
