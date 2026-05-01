package io.github.sophon.fightingnerd

import io.github.sophon.core.coreModule
import io.github.sophon.fightingnerd.featureRegistry.featureRegistryModule
import io.github.sophon.fightingnerd.screens.screensModule
import io.github.sophon.wikiSuperCombo.integration.superComboModule
import io.github.sophon.wikiwavu.integration.wavuModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

internal fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        platformModule,

        coreModule,
        wavuModule(),
        superComboModule(),

        screensModule,

        featureRegistryModule,
    )
}

internal expect val platformModule: Module