package io.github.sophon.wikiwavu.integration

import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.WavuWikiDataSourceImpl
import io.github.sophon.wikiwavu.domain.WavuWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun wavuModule() = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()

    factory<WikiClient>(named(WikiClientFeature.Wavu.id)) { params ->
        WavuWikiClient(
            game = params.get(),
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
        )
    }
}

