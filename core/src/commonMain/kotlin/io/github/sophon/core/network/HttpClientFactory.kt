package io.github.sophon.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
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
//                headers { append("x-access-token", API_KEY) } //API KEY
                contentType(ContentType.Application.Json)
            }
        }
    }
}


private const val TIMEOUT_IN_MILIS = 20_000L