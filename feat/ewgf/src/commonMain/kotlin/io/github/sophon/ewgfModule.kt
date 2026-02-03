package io.github.sophon

import org.koin.core.module.Module
import org.koin.dsl.module

fun ewgfModule() = module {
    includes(platformModule)

    //TODO
}

expect val platformModule: Module