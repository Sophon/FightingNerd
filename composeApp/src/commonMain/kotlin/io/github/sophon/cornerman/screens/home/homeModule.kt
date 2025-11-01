package io.github.sophon.cornerman.screens.home

import io.github.sophon.cornerman.featureRegistry.RegisteredFeature
import io.github.sophon.cornerman.featureRegistry.wavuWiki.WavuWikiFeature
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    single<List<RegisteredFeature>> {
        listOf(
            WavuWikiFeature(),
        )
    }

    viewModelOf(::HomeVM)
}