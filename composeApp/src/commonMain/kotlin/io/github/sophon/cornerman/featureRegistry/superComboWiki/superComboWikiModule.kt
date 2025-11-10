package io.github.sophon.cornerman.featureRegistry.superComboWiki

import io.github.sophon.cornerman.featureRegistry.superComboWiki.ui.SuperComboHomeVM
import io.github.sophon.cornerman.featureRegistry.superComboWiki.usecase.FetchCharacterListUseCase
import io.github.sophon.cornerman.featureRegistry.superComboWiki.usecase.SyncDataIfOldUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val superComboFeatureModule = module {
    viewModelOf(::SuperComboHomeVM)

    singleOf(::SyncDataIfOldUseCase)
    singleOf(::FetchCharacterListUseCase)
}