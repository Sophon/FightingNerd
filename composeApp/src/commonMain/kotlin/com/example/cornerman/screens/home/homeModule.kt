package com.example.cornerman.screens.home

import com.example.cornerman.featureRegistry.RegisteredFeature
import com.example.cornerman.featureRegistry.wavuWiki.WavuWikiFeature
import com.example.cornerman.screens.home.ui.HomeVM
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