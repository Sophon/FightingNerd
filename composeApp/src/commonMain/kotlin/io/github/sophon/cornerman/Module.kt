package io.github.sophon.cornerman

import io.github.sophon.core.coreModule
import io.github.sophon.cornerman.featureRegistry.featureRegistryModule
import io.github.sophon.cornerman.screens.home.homeModule
import io.github.sophon.cornerman.screens.moveList.moveListModule
import io.github.sophon.wikiwavu.wavuModule
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

        featureRegistryModule,
    )
}

expect val platformModule: Module