package com.example.wikiwavu

import com.example.core.coreModule
import com.example.wikiwavu.data.WavuWikiDataSource
import com.example.wikiwavu.data.WavuWikiDataSourceImpl
import com.example.wikiwavu.domain.WavuUrlProvider
import com.example.wikiwavu.usecase.CacheMoveListUseCase
import com.example.wikiwavu.usecase.ClearCacheUseCase
import com.example.wikiwavu.usecase.DownloadCharacterListUseCase
import com.example.wikiwavu.usecase.DownloadMoveListUseCase
import com.example.wikiwavu.usecase.FetchMoveDataUseCase
import com.example.wikiwavu.usecase.FetchMoveListUseCase
import com.example.wikiwavu.usecase.FetchMovesWithPropertyUseCase
import com.example.wikiwavu.usecase.GetLastCacheInsertInstantUseCase
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
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()
//    singleOf(::InMemoryMoveListDB).bind<com.example.wikiWavu.MoveListDB>()

    singleOf(::DownloadCharacterListUseCase)
    singleOf(::DownloadMoveListUseCase)
    singleOf(::CacheMoveListUseCase)
    singleOf(::GetLastCacheInsertInstantUseCase)
    singleOf(::ClearCacheUseCase)
    singleOf(::FetchMoveDataUseCase)
    singleOf(::FetchMovesWithPropertyUseCase)
    singleOf(::FetchMoveListUseCase)

    singleOf(::WavuUrlProvider)
}