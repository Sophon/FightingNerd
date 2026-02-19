package io.github.sophon

import io.github.sophon.data.local.DatabaseDriverFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val ewgfPlatformModule = module {
    singleOf(::DatabaseDriverFactory)
}