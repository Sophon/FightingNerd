package com.example.cornerman.screens.home

import com.example.cornerman.screens.home.usecase.FetchCharacterListUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)

    singleOf(::FetchCharacterListUseCase)
}