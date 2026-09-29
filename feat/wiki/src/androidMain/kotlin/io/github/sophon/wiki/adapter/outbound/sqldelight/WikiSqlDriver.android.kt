package io.github.sophon.wiki.adapter.outbound.sqldelight

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.sophon.wiki.data.WikiDB
import org.koin.core.scope.Scope
import java.io.File

internal actual fun Scope.createWikiSqlDriver(databaseDirectory: String?): SqlDriver {
    val context = get<Context>()
    val name = if (databaseDirectory == null) {
        WIKI_DATABASE_NAME
    } else {
        File(databaseDirectory, WIKI_DATABASE_NAME).absolutePath
    }

    val driver = openFingerprintedDriver(
        open = {
            AndroidSqliteDriver(
                schema = WikiDB.Schema,
                context = context,
                name = name,
                callback = object : AndroidSqliteDriver.Callback(WikiDB.Schema) {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        db.setForeignKeyConstraintsEnabled(true)
                    }
                },
            )
        },
        delete = { context.deleteDatabase(name) },
    )
    return driver
}
