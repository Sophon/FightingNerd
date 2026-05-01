package io.github.sophon.integration

import io.github.sophon.data.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseDriverFactory(databasePath = DatabaseDriverFactory.getDatabasePath()) }
}
