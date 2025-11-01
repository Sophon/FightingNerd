package io.github.sophon.core

import io.github.sophon.core.network.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val coreModule = module {
    single<HttpClientEngine> { httpClientEngine() }
    single<HttpClient> { HttpClientFactory.create(get(), get()) }
    single<Json> { Json { ignoreUnknownKeys = true } }
}

expect fun httpClientEngine(): HttpClientEngine