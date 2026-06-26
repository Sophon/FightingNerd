package io.github.sophon.fightingnerd.core.data.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun create(
        databaseName: String,
        schema: SqlSchema<QueryResult.Value<Unit>>,
    ): SqlDriver {
        val driver: SqlDriver = NativeSqliteDriver(
            schema = schema,
            name = databaseName,
        )
        return driver
    }
}
