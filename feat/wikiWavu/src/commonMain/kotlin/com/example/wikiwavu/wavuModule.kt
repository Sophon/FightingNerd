package com.example.wikiwavu

import com.example.core.coreModule
import com.example.wikiwavu.data.WavuWikiDataSource
import com.example.wikiwavu.data.WavuWikiDataSourceImpl
import com.example.wikiwavu.domain.Scheduler
import com.example.wikiwavu.domain.WavuUrlProvider
import com.example.wikiwavu.usecase.CacheMoveListUseCase
import com.example.wikiwavu.usecase.DownloadMoveListUseCase
import com.example.wikiwavu.usecase.DownloadCharacterListUseCase
import com.example.wikiwavu.usecase.FetchMoveDataUseCase
import com.example.wikiwavu.usecase.FetchMovesWithPropertyUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        wavuModule,
    )
}

val wavuModule = module {
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()
//    singleOf(::InMemoryMoveListDB).bind<com.example.wikiWavu.MoveListDB>()

    singleOf(::DownloadCharacterListUseCase)
    singleOf(::DownloadMoveListUseCase)
    singleOf(::CacheMoveListUseCase)
    singleOf(::FetchMoveDataUseCase)
    singleOf(::FetchMovesWithPropertyUseCase)

    singleOf(::WavuUrlProvider)
    singleOf(::Scheduler)
}