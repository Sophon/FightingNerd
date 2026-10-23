package io.github.sophon.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(engine: HttpClientEngine, json: Json): HttpClient {
        return HttpClient(engine) {
            install(Logging) {
                logger = Logger.DEFAULT
//                level = LogLevel.ALL
                level = LogLevel.NONE
            }

            install(ContentNegotiation) {
                json(json)
                json(json, contentType = ContentType.Text.Plain)
            }

            // before HttpTimeout - the first installed wraps the rest, so the timeout stays per attempt
            install(HttpRequestRetry) {
                maxRetries = MAX_TOO_MANY_REQUESTS_RETRIES
                retryIf { _, response -> response.status == HttpStatusCode.TooManyRequests }
                retryOnExceptionIf { _, _ -> false }
                exponentialDelay(baseDelayMs = TOO_MANY_REQUESTS_BASE_DELAY_IN_MILIS)
            }

            install(
                plugin = HttpTimeout,
                configure = {
                    socketTimeoutMillis = TIMEOUT_IN_MILIS
                    requestTimeoutMillis = TIMEOUT_IN_MILIS
                }
            )

            install(HttpCache)

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}


private const val TIMEOUT_IN_MILIS = 20_000L
private const val MAX_TOO_MANY_REQUESTS_RETRIES = 3
private const val TOO_MANY_REQUESTS_BASE_DELAY_IN_MILIS = 2_000L