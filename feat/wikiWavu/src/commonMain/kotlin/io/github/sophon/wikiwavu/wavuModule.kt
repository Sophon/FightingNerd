package io.github.sophon.wikiwavu

import io.github.sophon.core.domain.map
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.WavuWikiDataSourceImpl
import io.github.sophon.wikiwavu.data.toDomain
import io.github.sophon.wikiwavu.domain.WavuFeatureInfo
import io.github.sophon.wikiwavu.domain.WavuUrlProvider
import io.github.sophon.wikiwavu.usecase.CacheCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.CacheMoveListUseCase
import io.github.sophon.wikiwavu.usecase.ClearCacheUseCase
import io.github.sophon.wikiwavu.usecase.FetchCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.FetchCharacterUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveListUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveUseCase
import io.github.sophon.wikiwavu.usecase.GetLastCacheInsertInstantUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun wavuModule() = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::WavuWikiClient).bind<WikiClient>()
    single { WavuFeatureInfo }
    single { WavuUrlProvider }

    factory {

    }

    factory<WikiClient>(named("wavu")) { params ->
        val gameId: String = params.get()
        val charListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: WavuWikiDataSource = get()

        val downloadCharacterListUseCase = DownloadCharacterListUseCase(
            downloadAndMap = { queryTable ->
                source.downloadCharacterList()
                    .map { dto -> dto.toDomain() }
            }
        )

        val downloadMoveListUseCase = DownloadMoveListUseCase(
            downloadAndMap = { queryTable, charName ->
                source.downloadMoveList(queryTable.moves, charName)
                    .map { dto ->  dto.toDomain(charName) }
            }
        )

        WavuWikiClient(
            gameId = gameId,

            wavuFeatureInfo = get(),

            downloadCharacterListUseCase = downloadCharacterListUseCase,
            cacheCharacterListUseCase = CacheCharacterListUseCase(charListDB),
            fetchCharacterListUseCase = FetchCharacterListUseCase(charListDB),
            fetchCharacterUseCase = FetchCharacterUseCase(charListDB),

            downloadMoveListUseCase = downloadMoveListUseCase,
            cacheMoveListUseCase = CacheMoveListUseCase(moveListDB),
            fetchMoveListUseCase = FetchMoveListUseCase(moveListDB),
            fetchMoveUseCase = FetchMoveUseCase(moveListDB),

            getLastCacheInsertInstantUseCase = GetLastCacheInsertInstantUseCase(moveListDB),
            clearCacheUseCase = ClearCacheUseCase(charListDB, moveListDB)
        )
    }
}
