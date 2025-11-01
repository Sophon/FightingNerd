package com.example.glossaryinfil

import io.github.sophon.core.coreModule
import com.example.glossaryinfil.data.InfilGlossaryDataSource
import com.example.glossaryinfil.data.InfilGlossaryDataSourceImpl
import com.example.glossaryinfil.domain.InfilUrlProvider
import com.example.glossaryinfil.usecase.CacheGlossaryUseCase
import com.example.glossaryinfil.usecase.DownloadGlossaryUseCase
import com.example.glossaryinfil.usecase.FetchDataForTermUseCase
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