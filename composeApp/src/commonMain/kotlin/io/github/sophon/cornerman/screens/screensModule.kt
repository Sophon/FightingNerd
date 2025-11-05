package io.github.sophon.cornerman.screens

import io.github.sophon.cornerman.screens.home.homeModule
import io.github.sophon.cornerman.screens.moveList.moveListModule
import io.github.sophon.cornerman.screens.settings.ui.SettingsVM
import io.github.sophon.cornerman.screens.settings.usecase.GetAvailableFeaturesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val screensModule = module {
    includes(
        homeModule,
        moveListModule,
    )

    singleOf(::GetAvailableFeaturesUseCase)

    viewModelOf(::SettingsVM)
}