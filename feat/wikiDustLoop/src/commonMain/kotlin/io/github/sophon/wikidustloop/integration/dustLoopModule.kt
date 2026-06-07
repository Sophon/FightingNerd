package io.github.sophon.wikidustloop.integration

import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikidustloop.data.DustLoopDataSource
import io.github.sophon.wikidustloop.data.DustLoopDataSourceImpl
import io.github.sophon.wikidustloop.domain.DustLoopWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun dustLoopModule() = module {
    singleOf(::DustLoopDataSourceImpl).bind<DustLoopDataSource>()
    singleOf(::DustLoopWikiClient).bind<WikiClient>()
    single { DustLoopFeatureInfo }

    factory<WikiClient>(named(WikiClientFeature.DustLoop.id)) { params ->
        DustLoopWikiClient(
            game = params.get(),
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
        )
    }
}
