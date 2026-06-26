package io.github.sophon.xko.integration

import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.XkoWikiDataSourceImpl
import io.github.sophon.xko.domain.XkoWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun xkoModule() = module {
    singleOf(::XkoWikiDataSourceImpl).bind<XkoWikiDataSource>()
    singleOf(::XkoWikiClient).bind<WikiClient>()
    single { XkoFeatureInfo }

    factory<WikiClient>(named(WikiClientFeature.Xko.id)) { params ->
        XkoWikiClient(
            game = params.get(),
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
        )
    }
}
