package io.github.sophon.fightingnerd.screens

import io.github.sophon.fightingnerd.screens.home.homeModule
import io.github.sophon.fightingnerd.screens.moveList.moveListModule
import io.github.sophon.fightingnerd.screens.settings.ui.SettingsVM
import io.github.sophon.fightingnerd.screens.settings.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.screens.settings.usecase.ToggleFeatureUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val screensModule = module {
    includes(
        homeModule,
        moveListModule,
    )

    singleOf(::GetAvailableFeaturesUseCase)
    singleOf(::ToggleFeatureUseCase)

    viewModelOf(::SettingsVM)
}