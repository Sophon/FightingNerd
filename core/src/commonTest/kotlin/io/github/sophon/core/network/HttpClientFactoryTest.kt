package io.github.sophon.core.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.test.Test

class HttpClientFactoryTest {

    @Test
    fun `too many requests is retried until success`() = runTest {
        //given
        val statusList = listOf(HttpStatusCode.TooManyRequests, HttpStatusCode.OK)
        var callCount = 0
        val engine = MockEngine {
            val status = statusList[callCount]
            callCount++
            respond(content = "", status = status)
        }
        val httpClient = HttpClientFactory.create(engine, Json)
        val expected = HttpStatusCode.OK

        //when
        // real time - the virtual clock jumps to the request timeout while the engine responds on another dispatcher
        val result = withContext(Dispatchers.Default) { httpClient.get(URL).status }

        //then
        assertThat(result).isEqualTo(expected)
        assertThat(callCount).isEqualTo(2)
    }

    @Test
    fun `server error is not retried`() = runTest {
        //given
        var callCount = 0
        val engine = MockEngine {
            callCount++
            respond(content = "", status = HttpStatusCode.InternalServerError)
        }
        val httpClient = HttpClientFactory.create(engine, Json)
        val expected = HttpStatusCode.InternalServerError

        //when
        val result = httpClient.get(URL).status

        //then
        assertThat(result).isEqualTo(expected)
        assertThat(callCount).isEqualTo(1)
    }
}


private const val URL = "https://www.dustloop.com/wiki/api.php"
