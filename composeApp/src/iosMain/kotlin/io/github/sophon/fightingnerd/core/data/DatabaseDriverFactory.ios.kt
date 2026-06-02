package io.github.sophon.fightingnerd.core.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.github.sophon.fightingnerd.db.character.CharacterDatabase

actual class DatabaseDriverFactory {
    actual fun create(databaseName: String): SqlDriver {
        val driver: SqlDriver = NativeSqliteDriver(
            schema = CharacterDatabase.Schema,
            name = databaseName,
        )
        return driver
    }
}
