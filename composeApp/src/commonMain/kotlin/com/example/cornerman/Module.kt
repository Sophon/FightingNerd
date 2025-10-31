package com.example.cornerman

import com.example.core.coreModule
import com.example.cornerman.featureRegistry.wavuWiki.wavuWikiFeatureModule
import com.example.cornerman.screens.home.homeModule
import com.example.cornerman.screens.moveList.moveListModule
import com.example.wikiwavu.wavuModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        platformModule,
        coreModule,
        wavuModule,
        moveListModule(),
        homeModule,

        wavuWikiFeatureModule,
    )
}

expect val platformModule: Module