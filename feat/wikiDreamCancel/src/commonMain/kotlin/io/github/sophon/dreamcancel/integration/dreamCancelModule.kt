package io.github.sophon.dreamcancel.integration

import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSource
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSourceImpl
import io.github.sophon.dreamcancel.domain.DreamCancelWikiClient
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun dreamCancelModule() = module {
    singleOf(::DreamCancelWikiDataSourceImpl).bind<DreamCancelWikiDataSource>()

    factory<WikiClient>(named(WikiClientFeature.DreamCancel.id)) { params ->
        DreamCancelWikiClient(
            game = params.get(),
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
        )
    }
}
