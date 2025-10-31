package com.example.cornerman.screens.home

import com.example.cornerman.screens.home.ui.HomeVM
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)
}