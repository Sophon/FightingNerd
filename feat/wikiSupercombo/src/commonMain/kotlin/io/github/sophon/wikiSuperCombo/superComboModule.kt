package io.github.sophon.wikiSuperCombo

import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSourceImpl
import io.github.sophon.wikiSuperCombo.usecase.CacheCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

fun superComboModule(dbQualifier: Qualifier? = null) = module {
    singleOf(::SuperComboWikiClientImpl).bind<SuperComboWikiClient>()
    singleOf(::SuperComboDataSourceImpl).bind<SuperComboDataSource>()

    singleOf(::DownloadCharacterListUseCase)
    factory { CacheCharacterListUseCase(get(dbQualifier)) }
    factory { FetchCharacterUseCase(get(dbQualifier)) }
    singleOf(::DownloadMoveListUseCase)
}