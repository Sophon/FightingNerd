package io.github.sophon.cornerman.screens.home

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)
}