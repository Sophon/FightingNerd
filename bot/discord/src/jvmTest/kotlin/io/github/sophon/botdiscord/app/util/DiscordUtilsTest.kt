package io.github.sophon.botdiscord.app.util

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import dev.kord.core.exception.EntityNotFoundException
import io.github.sophon.discord.app.util.kordRestCall
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DiscordUtilsTest {
    @Test
    fun `block runs`() = runTest {
        // given
        var callCount = 0

        // when
        kordRestCall(tag = TAG) { callCount++ }

        // then
        assertThat(callCount).isEqualTo(1)
    }

    @Test
    fun `missing interaction is swallowed`() = runTest {
        // given
        val block: suspend () -> Unit = {
            throw EntityNotFoundException("Interaction with id 1290000000000000000 was not found.")
        }

        // when
        val result = kordRestCall(tag = TAG, block = block)

        // then
        assertThat(result).isEqualTo(Unit)
    }

    @Test
    fun `non-kord failure is rethrown`() = runTest {
        // given
        val block: suspend () -> Unit = { error("Embed is longer than 6000 characters") }

        // when
        val result = assertFailure { kordRestCall(tag = TAG, block = block) }

        // then
        result.isInstanceOf(IllegalStateException::class)
    }
}


private const val TAG = "DiscordUtilsTest"
