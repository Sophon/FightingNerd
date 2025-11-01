package io.github.sophon.glossaryinfil

import io.github.sophon.core.coreModule
import io.github.sophon.glossaryinfil.data.InfilGlossaryDataSource
import io.github.sophon.glossaryinfil.data.InfilGlossaryDataSourceImpl
import io.github.sophon.glossaryinfil.domain.InfilUrlProvider
import io.github.sophon.glossaryinfil.usecase.CacheGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.DownloadGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.FetchDataForTermUseCase
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    //add Koin modules here
    modules(
        infilModule,
        coreModule,
    )
}

val infilModule = module {
    singleOf(::InfilGlossaryDataSourceImpl).bind<InfilGlossaryDataSource>()
    singleOf(::InfilGlossaryImpl).bind<InfilGlossary>()

    singleOf(::DownloadGlossaryUseCase)
    singleOf(::CacheGlossaryUseCase)
    singleOf(::FetchDataForTermUseCase)

    singleOf(::InfilUrlProvider)
}