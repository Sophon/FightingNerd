package io.github.sophon.core

import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.CoreFeatureRepoImpl
import io.github.sophon.core.featureConfig.CoreWikiClientFactory
import io.github.sophon.core.network.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreModule = module {
    single<HttpClientEngine> { httpClientEngine() }
    single<HttpClient> { HttpClientFactory.create(get(), get()) }
    single<Json> { Json { ignoreUnknownKeys = true; prettyPrint = true } }

    singleOf(::CoreWikiClientFactory)
    singleOf(::CoreFeatureRepoImpl) { bind<CoreFeatureRepo>() }
}

expect fun httpClientEngine(): HttpClientEngine