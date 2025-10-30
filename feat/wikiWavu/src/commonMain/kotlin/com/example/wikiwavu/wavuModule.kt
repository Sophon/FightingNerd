package com.example.wikiwavu

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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wavuModule = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()

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