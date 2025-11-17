package io.github.sophon.fightingnerd.screens.home

import io.github.sophon.fightingnerd.screens.home.usecase.GetAvailableFeaturesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)

    singleOf(::GetAvailableFeaturesUseCase)
}