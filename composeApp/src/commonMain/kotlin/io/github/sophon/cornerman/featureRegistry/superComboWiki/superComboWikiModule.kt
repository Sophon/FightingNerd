package io.github.sophon.cornerman.featureRegistry.superComboWiki

import io.github.sophon.cornerman.QUALIFIER_SC
import io.github.sophon.cornerman.featureRegistry.superComboWiki.ui.SuperComboHomeVM
import io.github.sophon.cornerman.featureRegistry.superComboWiki.usecase.FetchCharacterListUseCase
import io.github.sophon.cornerman.featureRegistry.superComboWiki.usecase.SyncDataIfOldUseCase
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchMoveListUseCase
import io.github.sophon.wikiSuperCombo.SuperComboWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val superComboFeatureModule = module {
    viewModelOf(::SuperComboHomeVM)

    singleOf(::SyncDataIfOldUseCase)
    singleOf(::FetchCharacterListUseCase)

    single<FetchMoveListUseCase>(named(QUALIFIER_SC)) {
        FetchMoveListUseCase(get<SuperComboWikiClient>()::fetchMoveListFor)
    }
}