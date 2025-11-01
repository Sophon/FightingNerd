package io.github.sophon.cornerman

import com.example.core.coreModule
import io.github.sophon.cornerman.featureRegistry.wavuWiki.wavuWikiFeatureModule
import io.github.sophon.cornerman.screens.home.homeModule
import io.github.sophon.cornerman.screens.moveList.moveListModule
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