package io.github.sophon.cornerman.screens.home

import io.github.sophon.cornerman.screens.home.usecase.GetAvailableFeaturesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)

    singleOf(::GetAvailableFeaturesUseCase)
}