package com.example.cornerman

import com.example.core.coreModule
import com.example.cornerman.screens.moveList.moveListModule
import com.example.wikiwavu.wavuModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        wavuModule,
        moveListModule(),
    )
}