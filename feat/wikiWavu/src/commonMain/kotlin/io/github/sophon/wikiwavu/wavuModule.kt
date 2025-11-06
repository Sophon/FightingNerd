package io.github.sophon.wikiwavu

import io.github.sophon.core.data.WikiDataSource
import io.github.sophon.core.domain.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.domain.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.data.MoveListResponseDto
import io.github.sophon.wikiwavu.data.WavuWikiDataSourceImpl
import io.github.sophon.wikiwavu.data.toDomain
import io.github.sophon.wikiwavu.domain.WavuUrlProvider
import io.github.sophon.wikiwavu.usecase.CacheMoveListUseCase
import io.github.sophon.wikiwavu.usecase.ClearCacheUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveDataUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveListUseCase
import io.github.sophon.wikiwavu.usecase.FetchMovesWithPropertyUseCase
import io.github.sophon.wikiwavu.usecase.GetLastCacheInsertInstantUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wavuModule = module {
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()
    single<WikiDataSource<CharacterListResponseDto, MoveListResponseDto>> {
        WavuWikiDataSourceImpl(
            httpClient = get(),
        )
    }

    single {
        DownloadCharacterListUseCase<CharacterListResponseDto, WavuError>(
            source = get(),
            toDomain = { toDomain() },
            toDomainError = { toDomain() },
        )
    }
    single {
        DownloadMoveListUseCase<MoveListResponseDto, WavuError>(
            source = get(),
            toDomain = { dto, charName -> toDomain(dto, charName) },
            toDomainError = { toDomain() }
        )
    }
    singleOf(::CacheMoveListUseCase)
    singleOf(::GetLastCacheInsertInstantUseCase)
    singleOf(::ClearCacheUseCase)
    singleOf(::FetchMoveDataUseCase)
    singleOf(::FetchMovesWithPropertyUseCase)
    singleOf(::FetchMoveListUseCase)

    singleOf(::WavuUrlProvider)
}