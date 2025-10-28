package com.example.cornerman

import com.example.cornerman.moveList.moveListModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        moveListModule(),
    )
}