package io.github.sophon.wikiwavu

import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.wikiwavu.domain.WavuUrlProvider
import io.github.sophon.wikiwavu.usecase.CacheCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.CacheMoveListUseCase
import io.github.sophon.wikiwavu.usecase.ClearCacheUseCase
import io.github.sophon.wikiwavu.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.usecase.FetchCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveDataUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveListUseCase
import io.github.sophon.wikiwavu.usecase.GetFeatureInfoUseCase
import io.github.sophon.wikiwavu.usecase.GetLastCacheInsertInstantUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

fun wavuModule(dbQualifier: Qualifier? = null) = module {
    singleOf(::WavuWikiClient).bind<WikiClient>()

    singleOf(::DownloadCharacterListUseCase)
    factory { CacheCharacterListUseCase(get(dbQualifier)) }
    factory { FetchCharacterListUseCase(get(dbQualifier)) }
    singleOf(::DownloadMoveListUseCase)
    factory { CacheMoveListUseCase(get(dbQualifier)) }
    factory { GetLastCacheInsertInstantUseCase(get(dbQualifier)) }
    factory { ClearCacheUseCase(get(dbQualifier), get(dbQualifier)) }
    factory { FetchMoveDataUseCase(get(dbQualifier)) }
    factory { FetchMoveListUseCase(get(dbQualifier)) }
    singleOf(::GetFeatureInfoUseCase)

    singleOf(::WavuUrlProvider)
}