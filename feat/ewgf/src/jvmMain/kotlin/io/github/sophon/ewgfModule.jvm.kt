package io.github.sophon

import io.github.sophon.adapter.outbound.sqldelight.EwgfDatabaseDriverFactory
import org.koin.dsl.module

internal actual val ewgfPlatformModule = module {
    single { EwgfDatabaseDriverFactory(databasePath = EwgfDatabaseDriverFactory.getDatabasePath()) }
}
