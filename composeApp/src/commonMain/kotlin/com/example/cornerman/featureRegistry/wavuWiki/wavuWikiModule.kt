package com.example.cornerman.featureRegistry.wavuWiki

import com.example.cornerman.screens.home.domain.usecase.FetchCharacterListUseCase
import com.example.cornerman.screens.home.domain.usecase.StartWavuSessionUseCase
import com.example.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val wavuWikiFeatureModule = module {
    viewModelOf(::WavuHomeScreenVM)

    singleOf(::StartWavuSessionUseCase)
    singleOf(::FetchCharacterListUseCase)

    singleOf(::FileReaderKMP).bind<FileReader>()
}