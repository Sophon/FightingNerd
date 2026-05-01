package io.github.sophon.fightingnerd.featureRegistry.wavuWiki

import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.ui.WavuHomeScreenVM
import io.github.sophon.wikiwavu.integration.data.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val wavuComposeModule = module {
    viewModelOf(::WavuHomeScreenVM)
    singleOf(::FileReaderKMP).bind<FileReader>()
}