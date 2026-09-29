package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.sophon.wiki.data.WikiDB
import org.koin.core.scope.Scope
import java.io.File
import java.util.Properties

internal actual fun Scope.createWikiSqlDriver(databaseDirectory: String?): SqlDriver {
    val databaseFile = File(databaseDirectory ?: ".", WIKI_DATABASE_NAME)
    databaseFile.parentFile?.mkdirs()

    val driver = openFingerprintedDriver(
        open = {
            JdbcSqliteDriver(
                url = "jdbc:sqlite:${databaseFile.absolutePath}",
                properties = Properties().apply { put("foreign_keys", "true") },
                schema = WikiDB.Schema,
            )
        },
        delete = { databaseFile.delete() },
    )
    return driver
}
