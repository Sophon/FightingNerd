package io.github.sophon.fightingnerd

import io.github.sophon.core.coreModule
import io.github.sophon.fightingnerd.featureRegistry.featureRegistryModule
import io.github.sophon.fightingnerd.screens.screensModule
import io.github.sophon.wikiSuperCombo.superComboModule
import io.github.sophon.wikiwavu.wavuModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration

internal const val QUALIFIER_WAVU = "wavu"
internal const val QUALIFIER_SC = "superCombo"

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
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

expect val platformModule: Module