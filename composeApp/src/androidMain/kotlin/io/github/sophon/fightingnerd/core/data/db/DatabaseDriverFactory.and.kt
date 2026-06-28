package io.github.sophon.fightingnerd.core.data.db

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun create(
        databaseName: String,
        schema: SqlSchema<QueryResult.Value<Unit>>,
    ): SqlDriver {
        val callback = object : AndroidSqliteDriver.Callback(schema) {
            override fun onUpgrade(
                db: SupportSQLiteDatabase,
                oldVersion: Int,
                newVersion: Int,
            ) {
                val tables = mutableListOf<String>()
                db.query(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name != 'android_metadata'"
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        tables.add(cursor.getString(0))
                    }
                }
                tables.forEach { tableName ->
                    db.execSQL("DROP TABLE IF EXISTS $tableName")
                }
                schema.create(AndroidSqliteDriver(database = db))
            }
        }

        val driver: SqlDriver = AndroidSqliteDriver(
            schema = schema,
            context = context,
            name = databaseName,
            callback = callback,
        )
        return driver
    }
}
