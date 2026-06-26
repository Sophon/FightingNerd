package io.github.sophon.integration

import io.github.sophon.data.local.DatabaseDriverFactory
import org.koin.dsl.module

actual val ewgfPlatformModule = module {
    single {
        DatabaseDriverFactory(databasePath = DatabaseDriverFactory.getDatabasePath())
    }
}