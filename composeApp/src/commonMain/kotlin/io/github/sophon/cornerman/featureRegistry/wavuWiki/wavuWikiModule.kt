package io.github.sophon.cornerman.featureRegistry.wavuWiki

import io.github.sophon.cornerman.featureRegistry.wavuWiki.ui.WavuHomeScreenVM
import io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase.FetchCharacterListUseCase
import io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase.SyncDataIfOldUseCase
import io.github.sophon.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val wavuWikiFeatureModule = module {
    viewModelOf(::WavuHomeScreenVM)

    singleOf(::SyncDataIfOldUseCase)
    singleOf(::FetchCharacterListUseCase)

    singleOf(::FileReaderKMP).bind<FileReader>()
}