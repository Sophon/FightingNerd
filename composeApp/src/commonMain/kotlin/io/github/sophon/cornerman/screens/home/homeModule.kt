package io.github.sophon.cornerman.screens.home

import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.cornerman.featureRegistry.wavuWiki.WavuWikiFeatureCompose
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    single<List<ComposeRegisteredFeature>> {
        listOf(
            WavuWikiFeatureCompose(),
        )
    }

    viewModelOf(::HomeVM)
}