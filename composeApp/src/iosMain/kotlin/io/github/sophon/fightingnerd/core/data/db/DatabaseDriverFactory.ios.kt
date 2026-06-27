package io.github.sophon.fightingnerd.core.data.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection

actual class DatabaseDriverFactory {
    actual fun create(
        databaseName: String,
        schema: SqlSchema<QueryResult.Value<Unit>>,
    ): SqlDriver {
        val driver: SqlDriver = NativeSqliteDriver(
            schema = schema,
            name = databaseName,
            onConfiguration = { config ->
                config.copy(
                    upgrade = { connection, _, _ ->
                        wrapConnection(connection) { sqlDriver ->
                            val tables = mutableListOf<String>()
                            sqlDriver.executeQuery(
                                identifier = null,
                                sql = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",
                                parameters = 0,
                                mapper = { cursor ->
                                    while (cursor.next().value) {
                                        cursor.getString(0)?.let { tables.add(it) }
                                    }
                                    QueryResult.Unit
                                },
                            )
                            tables.forEach { tableName ->
                                sqlDriver.execute(
                                    identifier = null,
                                    sql = "DROP TABLE IF EXISTS $tableName",
                                    parameters = 0,
                                )
                            }
                            schema.create(sqlDriver)
                        }
                    },
                )
            },
        )
        return driver
    }
}
