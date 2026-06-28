package io.github.sophon.wikidragdown.integration

import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikidragdown.data.DragDownDataSource
import io.github.sophon.wikidragdown.data.DragDownDataSourceImpl
import io.github.sophon.wikidragdown.domain.DragDownWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun dragdownModule() = module {
    singleOf(::DragDownDataSourceImpl).bind<DragDownDataSource>()
    singleOf(::DragDownWikiClient).bind<WikiClient>()
    single { DragDownFeatureInfo }

    factory<WikiClient>(named(WikiClientFeature.DragDown.id)) { params ->
        DragDownWikiClient(
            game = params.get(),
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
        )
    }
}
