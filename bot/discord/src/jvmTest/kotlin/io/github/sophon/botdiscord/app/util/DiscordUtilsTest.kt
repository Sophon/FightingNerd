package io.github.sophon.botdiscord.app.util

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.json.response.DiscordErrorResponse
import dev.kord.rest.request.KtorRequestException
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.route.Route
import io.github.sophon.discord.app.util.kordRestCall
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
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

    @Test
    fun `forbidden whispers the discord error`() = runTest {
        // given
        val exception = kordException(status = HttpStatusCode.Forbidden)
        var whispered: String? = null

        // when
        kordRestCall(
            tag = TAG,
            onForbidden = { errorMessage -> whispered = errorMessage },
        ) { throw exception }

        // then
        assertThat(whispered).isNotNull().contains(MISSING_READ_HISTORY)
    }

    @Test
    fun `other kord failures don't whisper`() = runTest {
        // given
        val exception = kordException(status = HttpStatusCode.NotFound)
        var whispered: String? = null

        // when
        kordRestCall(
            tag = TAG,
            onForbidden = { errorMessage -> whispered = errorMessage },
        ) { throw exception }

        // then
        assertThat(whispered).isNull()
    }

    @Test
    fun `failed whisper is swallowed`() = runTest {
        // given
        val exception = kordException(status = HttpStatusCode.Forbidden)

        // when
        val result = kordRestCall(
            tag = TAG,
            onForbidden = { throw kordException(status = HttpStatusCode.Forbidden) },
        ) { throw exception }

        // then
        assertThat(result).isEqualTo(Unit)
    }
}


private suspend fun kordException(status: HttpStatusCode): KtorRequestException {
    val client = HttpClient(MockEngine { respond(content = "", status = status) })
    val httpResponse = client.get("https://discord.com/api/v10/users/@me")
    val exception = KtorRequestException(
        httpResponse = httpResponse,
        request = RequestBuilder(Route.CurrentUserGet).build(),
        discordError = DiscordErrorResponse(message = MISSING_READ_HISTORY),
    )
    return exception
}

private const val TAG = "DiscordUtilsTest"
private const val MISSING_READ_HISTORY = "Cannot reply without permission to read message history"
