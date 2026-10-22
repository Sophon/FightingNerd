package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.discord.app.outPort.RefreshWikiPort
import io.github.sophon.discord.app.service.RefreshWikiService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RefreshWikiServiceTest {
    @Test
    fun `wiki is refreshed once`() = runTest {
        // given
        val refreshWikiPort = FakeRefreshWikiPort()
        val service = RefreshWikiService(refreshWikiPort = refreshWikiPort)

        // when
        service()

        // then
        assertThat(refreshWikiPort.refreshCount).isEqualTo(1)
    }


    private class FakeRefreshWikiPort: RefreshWikiPort {
        var refreshCount = 0
            private set

        override suspend fun refresh() {
            refreshCount++
        }
    }
}
