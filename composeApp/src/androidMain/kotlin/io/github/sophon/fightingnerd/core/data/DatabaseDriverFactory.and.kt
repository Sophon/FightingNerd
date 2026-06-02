package io.github.sophon.fightingnerd.core.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.sophon.fightingnerd.db.character.CharacterDatabase

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun create(databaseName: String): SqlDriver {
        val driver: SqlDriver = AndroidSqliteDriver(
            schema = CharacterDatabase.Schema,
            context = context,
            name = databaseName,
        )
        return driver
    }
}
