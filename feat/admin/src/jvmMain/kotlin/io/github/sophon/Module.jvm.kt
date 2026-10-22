package io.github.sophon

import io.github.sophon.adapter.outbound.sqldelight.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseDriverFactory(databasePath = DatabaseDriverFactory.getDatabasePath()) }
}
