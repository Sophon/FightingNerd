package io.github.sophon.fightingnerd.featureRegistry.superComboWiki

import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui.SuperComboHomeVM
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase.FetchCharacterListUseCase
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase.GetSuperComboFeatureUseCase
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase.SyncDataIfOldUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val superComboFeatureModule = module {
    viewModelOf(::SuperComboHomeVM)

    singleOf(::GetSuperComboFeatureUseCase)
    singleOf(::SyncDataIfOldUseCase)
    singleOf(::FetchCharacterListUseCase)
}