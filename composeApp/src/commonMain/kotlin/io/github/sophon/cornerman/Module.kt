package io.github.sophon.cornerman

import io.github.sophon.core.coreModule
import io.github.sophon.cornerman.featureRegistry.featureRegistryModule
import io.github.sophon.cornerman.screens.screensModule
import io.github.sophon.wikiwavu.wavuModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration

internal const val QUALIFIER_WAVU = "wavu"

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        platformModule,

        coreModule,
        wavuModule(named(QUALIFIER_WAVU)),

        screensModule,

        featureRegistryModule,
    )
}

expect val platformModule: Module