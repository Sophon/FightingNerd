package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.sophon.wiki.data.WikiDB
import org.koin.core.scope.Scope
import java.io.File
import java.util.Properties

internal actual fun Scope.createWikiSqlDriver(databaseDirectory: String?): SqlDriver {
    val databaseFile = File(databaseDirectory ?: "db", WIKI_DATABASE_NAME)
    databaseFile.parentFile?.mkdirs()

    val driver = openFingerprintedDriver(
        open = { openWikiJdbcDriver(databaseFile) },
        delete = { deleteWikiDatabase(databaseFile) },
    )
    return driver
}

/**
 * WAL - reads see the last commit instead of waiting for a write in progress.
 * Every connection gets these properties - the driver opens one per thread.
 */
internal fun openWikiJdbcDriver(databaseFile: File): SqlDriver {
    val properties = Properties().apply {
        put("foreign_keys", "true")
        put("journal_mode", "WAL")
        put("busy_timeout", BUSY_TIMEOUT_MILLIS)
    }
    val driver = JdbcSqliteDriver(
        url = "jdbc:sqlite:${databaseFile.absolutePath}",
        properties = properties,
        schema = WikiDB.Schema,
    )
    return driver
}

/**
 * Deletes the WAL files too - a leftover WAL must not be applied to a new database.
 */
internal fun deleteWikiDatabase(databaseFile: File) {
    databaseFile.delete()
    File("${databaseFile.path}-wal").delete()
    File("${databaseFile.path}-shm").delete()
}


private const val BUSY_TIMEOUT_MILLIS = "5000"
