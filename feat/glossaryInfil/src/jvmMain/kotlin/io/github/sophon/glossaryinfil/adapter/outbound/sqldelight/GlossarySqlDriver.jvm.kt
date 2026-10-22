package io.github.sophon.glossaryinfil.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.sophon.glossaryinfil.data.GlossaryDB
import java.io.File

internal actual fun createGlossarySqlDriver(databaseDirectory: String?): SqlDriver {
    val databaseFile = File(databaseDirectory ?: "db", GLOSSARY_DATABASE_NAME)
    databaseFile.parentFile?.mkdirs()

    val driver = openFingerprintedDriver(
        open = {
            JdbcSqliteDriver(
                url = "jdbc:sqlite:${databaseFile.absolutePath}",
                schema = GlossaryDB.Schema,
            )
        },
        delete = { databaseFile.delete() },
    )
    return driver
}
