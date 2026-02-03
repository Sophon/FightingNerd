package io.github.sophon.data

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw NotImplementedError("Database not used on iOS - mobile apps call bot API")
    }
}