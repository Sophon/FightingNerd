package io.github.sophon.wikiSuperCombo

import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSourceImpl
import io.github.sophon.wikiSuperCombo.data.UrlResolver
import io.github.sophon.wikiSuperCombo.data.toDomain
import io.github.sophon.wikiSuperCombo.domain.SuperComboFeatureInfo
import io.github.sophon.wikiSuperCombo.usecase.CacheCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.CacheMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.ClearCacheUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchMoveUseCase
import io.github.sophon.wikiSuperCombo.usecase.GetLastCacheInsertInstantUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun superComboModule() = module {
    singleOf(::SuperComboDataSourceImpl).bind<SuperComboDataSource>()
    singleOf(::SuperComboWikiClient).bind<WikiClient>()
    single { SuperComboFeatureInfo }
    factoryOf(::UrlResolver)

    factory<WikiClient>(named("supercombo")) { params ->
        val gameId: String = params.get()
        val characterListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: SuperComboDataSource = get()
        val urlResolver: UrlResolver = get()

        val downloadMoveListUseCase = DownloadMoveListUseCase(
            downloadAndMap = { queryTable, charName ->
                source.downloadMoveList(queryTable.moves, charName)
                    .flatMap { dto ->
                        urlResolver.resolveHitboxUrl(dto)
                            .map { dto.toDomain(it) }
                    }
            }
        )

        SuperComboWikiClient(
            gameId = gameId,

            superComboFeatureInfo = get(),

            downloadCharacterListUseCase = DownloadCharacterListUseCase(get()),
            cacheCharacterListUseCase = CacheCharacterListUseCase(characterListDB),
            fetchCharacterUseCase = FetchCharacterUseCase(characterListDB),
            fetchCharacterListUseCase = FetchCharacterListUseCase(characterListDB),

            downloadMoveListUseCase = downloadMoveListUseCase,
            cacheMoveListUseCase = CacheMoveListUseCase(moveListDB),
            fetchMoveUseCase = FetchMoveUseCase(moveListDB),
            fetchMoveListUseCase = FetchMoveListUseCase(moveListDB),

            getLastCacheInsertInstantUseCase = GetLastCacheInsertInstantUseCase(moveListDB),
            clearCacheUseCase = ClearCacheUseCase(characterListDB, moveListDB),
        )
    }
}