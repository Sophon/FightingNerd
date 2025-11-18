package io.github.sophon.wikiSuperCombo

import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSourceImpl
import io.github.sophon.wikiSuperCombo.usecase.CacheCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.CacheMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.ClearCacheUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchMoveUseCase
import io.github.sophon.wikiSuperCombo.usecase.GetLastCacheInsertInstantUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

fun superComboModule(dbQualifier: Qualifier? = null) = module {
    singleOf(::SuperComboWikiClient).bind<WikiClient>()
    singleOf(::SuperComboDataSourceImpl).bind<SuperComboDataSource>()

    singleOf(::DownloadCharacterListUseCase)
    factory { CacheCharacterListUseCase(get(dbQualifier)) }
    factory { FetchCharacterUseCase(get(dbQualifier)) }
    factory { FetchCharacterListUseCase(get(dbQualifier)) }
    singleOf(::DownloadMoveListUseCase)
    factory { CacheMoveListUseCase(get(dbQualifier)) }
    factory { GetLastCacheInsertInstantUseCase(get(dbQualifier)) }
    factory {
        ClearCacheUseCase(get(dbQualifier), get(dbQualifier))
    }
    factory { FetchMoveUseCase(get(dbQualifier)) }
    factory { FetchMoveListUseCase(get(dbQualifier)) }
}