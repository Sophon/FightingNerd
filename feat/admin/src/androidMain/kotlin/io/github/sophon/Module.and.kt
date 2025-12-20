package io.github.sophon

import io.github.sophon.data.DatabaseDriverFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::DatabaseDriverFactory)
}