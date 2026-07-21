package io.github.sophon.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit

actual fun httpClientEngine(): HttpClientEngine {
    return OkHttp.create {
        config {
            retryOnConnectionFailure(true)
            connectTimeout(20, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            callTimeout(0, TimeUnit.SECONDS)
        }
    }
}