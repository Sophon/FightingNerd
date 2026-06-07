package io.github.sophon.wikimizuumi.integration

import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSource
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSourceImpl
import io.github.sophon.wikimizuumi.domain.MizuumiWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun mizuumiModule() = module {
    singleOf(::MizuumiWikiDataSourceImpl).bind<MizuumiWikiDataSource>()
    singleOf(::MizuumiWikiClient).bind<WikiClient>()
    single { MizuumiFeatureInfo }

    factory<WikiClient>(named(WikiClientFeature.Mizuumi.id)) { params ->
        MizuumiWikiClient(
            game = params.get(),
            characterDB = params.get(),
            moveDB = params.get(),
            source = get(),
        )
    }
}