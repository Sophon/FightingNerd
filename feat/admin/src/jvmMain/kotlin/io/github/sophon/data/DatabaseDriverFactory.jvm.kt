package io.github.sophon.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.sophon.admin.data.AdminDatabase

actual class DatabaseDriverFactory(private val databasePath: String) {
    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        // Force drop and recreate (destructive migrations)
        driver.execute(null, "DROP TABLE IF EXISTS ban", 0)
        AdminDatabase.Schema.create(driver)

        return driver
    }
}