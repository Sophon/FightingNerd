package io.github.sophon.xko

import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.XkoWikiDataSourceImpl
import io.github.sophon.xko.domain.XkoFeatureInfo
import io.github.sophon.xko.usecase.CacheCharacterListUseCase
import io.github.sophon.xko.usecase.CacheMoveListUseCase
import io.github.sophon.xko.usecase.ClearCacheUseCase
import io.github.sophon.xko.usecase.DownloadOrFetchUseCase
import io.github.sophon.xko.usecase.FetchCharacterListUseCase
import io.github.sophon.xko.usecase.FetchCharacterUseCase
import io.github.sophon.xko.usecase.FetchMoveListUseCase
import io.github.sophon.xko.usecase.FetchMoveUseCase
import io.github.sophon.xko.usecase.GetLastCacheInsertInstantUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun xkoModule() = module {
    singleOf(::XkoWikiDataSourceImpl).bind<XkoWikiDataSource>()
    singleOf(::XkoWikiClient).bind<WikiClient>()
    single { XkoFeatureInfo }

    singleOf(::DownloadOrFetchUseCase)

    factory<WikiClient>(named("xko")) { params ->
        val gameId: String = params.get()
        val charListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()

        XkoWikiClient(
            gameId = gameId,

            downloadOrFetchUseCase = DownloadOrFetchUseCase(get()),

            cacheCharacterListUseCase = CacheCharacterListUseCase(charListDB),
            fetchCharacterListUseCase = FetchCharacterListUseCase(charListDB),
            fetchCharacterUseCase = FetchCharacterUseCase(charListDB),

            cacheMoveListUseCase = CacheMoveListUseCase(moveListDB),
            fetchMoveListUseCase = FetchMoveListUseCase(moveListDB),
            fetchMoveUseCase = FetchMoveUseCase(moveListDB),

            getLastCacheInsertInstantUseCase = GetLastCacheInsertInstantUseCase(moveListDB),
            clearCacheUseCase = ClearCacheUseCase(moveListDB),
        )
    }
}