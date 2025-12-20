package io.github.sophon

import io.github.sophon.data.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseDriverFactory(databasePath = "admin.db") }
}
