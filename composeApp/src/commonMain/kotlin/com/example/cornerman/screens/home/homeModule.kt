package com.example.cornerman.screens.home

import com.example.cornerman.infrastructure.FileReaderKMP
import com.example.cornerman.screens.home.domain.usecase.FetchCharacterListUseCase
import com.example.cornerman.screens.home.domain.usecase.StartWavuSessionUseCase
import com.example.cornerman.screens.home.ui.HomeVM
import com.example.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)

    singleOf(::StartWavuSessionUseCase)
    singleOf(::FetchCharacterListUseCase)

    singleOf(::FileReaderKMP).bind<FileReader>()
}