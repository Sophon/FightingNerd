package io.github.sophon.cornerman.screens

import io.github.sophon.cornerman.screens.home.homeModule
import io.github.sophon.cornerman.screens.moveList.moveListModule
import org.koin.dsl.module

internal val screensModule = module {
    includes(
        homeModule,
        moveListModule,
    )
}