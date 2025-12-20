package io.github.sophon.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.sophon.admin.data.AdminDatabase

actual class DatabaseDriverFactory(private val databasePath: String) {
    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")
        AdminDatabase.Schema.create(driver)
        return driver
    }
}
