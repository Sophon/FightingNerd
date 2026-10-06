package io.github.sophon.glossaryinfil

import io.github.sophon.glossaryinfil.adapter.outbound.ktor.KtorGlossaryAdapter
import io.github.sophon.glossaryinfil.adapter.outbound.sqldelight.LazyGlossaryDB
import io.github.sophon.glossaryinfil.adapter.outbound.sqldelight.SqlDelightGlossaryAdapter
import io.github.sophon.glossaryinfil.adapter.outbound.sqldelight.createGlossarySqlDriver
import io.github.sophon.glossaryinfil.app.outPort.CountGlossaryItemsPort
import io.github.sophon.glossaryinfil.app.outPort.FetchGlossaryPort
import io.github.sophon.glossaryinfil.app.outPort.LoadGlossaryItemListPort
import io.github.sophon.glossaryinfil.app.outPort.ReplaceGlossaryPort
import io.github.sophon.glossaryinfil.app.service.RefreshGlossaryService
import io.github.sophon.glossaryinfil.app.service.SearchGlossaryService
import io.github.sophon.glossaryinfil.inPort.RefreshGlossaryUseCase
import io.github.sophon.glossaryinfil.inPort.SearchGlossaryUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * [databaseDirectory] - where `glossary.db` lives; null for the platform's default database location.
 */
fun infilModule(databaseDirectory: String? = null): Module = module {
    single { GlossaryFeatureInfo }

    singleOf(::RefreshGlossaryService).bind<RefreshGlossaryUseCase>()
    singleOf(::SearchGlossaryService).bind<SearchGlossaryUseCase>()

    singleOf(::KtorGlossaryAdapter).bind<FetchGlossaryPort>()
    single { LazyGlossaryDB { createGlossarySqlDriver(databaseDirectory) } }
    singleOf(::SqlDelightGlossaryAdapter) withOptions {
        bind<ReplaceGlossaryPort>()
        bind<LoadGlossaryItemListPort>()
        bind<CountGlossaryItemsPort>()
    }
}
