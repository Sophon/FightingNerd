package io.github.sophon.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver

internal expect class EwgfDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
