package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseFileContext
import io.github.sophon.wiki.data.WikiDB
import org.koin.core.scope.Scope

internal actual fun Scope.createWikiSqlDriver(databaseDirectory: String?): SqlDriver {
    val driver = openFingerprintedDriver(
        open = {
            NativeSqliteDriver(
                schema = WikiDB.Schema,
                name = WIKI_DATABASE_NAME,
                onConfiguration = { configuration ->
                    configuration.copy(
                        extendedConfig = configuration.extendedConfig.copy(
                            foreignKeyConstraints = true,
                            basePath = databaseDirectory,
                        ),
                    )
                },
            )
        },
        delete = { DatabaseFileContext.deleteDatabase(WIKI_DATABASE_NAME, databaseDirectory) },
    )
    return driver
}
