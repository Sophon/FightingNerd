package io.github.sophon.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.sophon.admin.data.AdminDatabase

actual class DatabaseDriverFactory(private val databasePath: String) {
    actual fun createDriver(): SqlDriver {
        val databaseFile = java.io.File(databasePath)
        val databaseExists = databaseFile.exists()

        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        if (databaseExists.not()) {
            AdminDatabase.Schema.create(driver)
        }

        return driver
    }
}
