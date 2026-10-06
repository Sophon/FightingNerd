package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.outPort.RefreshGlossaryPort
import io.github.sophon.discord.app.service.RefreshGlossaryService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RefreshGlossaryServiceTest {
    @Test
    fun `glossary is refreshed once`() = runTest {
        // given
        val refreshGlossaryPort = FakeRefreshGlossaryPort()
        val service = RefreshGlossaryService(refreshGlossaryPort = refreshGlossaryPort)

        // when
        service()

        // then
        assertThat(refreshGlossaryPort.refreshCount).isEqualTo(1)
    }


    private class FakeRefreshGlossaryPort: RefreshGlossaryPort {
        var refreshCount = 0
            private set

        override suspend fun refresh(): EmptyResult<BotError> {
            refreshCount++
            return Result.Success(Unit)
        }
    }
}
