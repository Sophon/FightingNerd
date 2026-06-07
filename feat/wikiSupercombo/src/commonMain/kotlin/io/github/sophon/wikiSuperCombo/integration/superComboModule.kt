package io.github.sophon.wikiSuperCombo.integration

import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSourceImpl
import io.github.sophon.wikiSuperCombo.domain.SuperComboWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun superComboModule() = module {
    singleOf(::SuperComboDataSourceImpl).bind<SuperComboDataSource>()
    singleOf(::SuperComboWikiClient).bind<WikiClient>()
    single { SuperComboFeatureInfo }

    factory<WikiClient> { params ->
        SuperComboWikiClient(
            game = params.get(),
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
        )
    }
}
