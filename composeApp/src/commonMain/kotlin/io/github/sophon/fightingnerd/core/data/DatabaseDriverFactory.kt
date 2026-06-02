package io.github.sophon.fightingnerd.core.data

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun create(databaseName: String): SqlDriver
}
