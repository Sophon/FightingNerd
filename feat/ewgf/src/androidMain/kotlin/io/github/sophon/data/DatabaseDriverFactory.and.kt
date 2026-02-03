package io.github.sophon.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        throw NotImplementedError(
            "Database not used on Android - mobile apps call bot API"
        )
    }
}