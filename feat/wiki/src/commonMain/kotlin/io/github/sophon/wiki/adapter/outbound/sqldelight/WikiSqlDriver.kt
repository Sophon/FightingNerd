package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver
import io.github.aakira.napier.Napier
import io.github.sophon.core.wiki.data.fingerprint
import io.github.sophon.core.wiki.data.readStoredFingerprint
import io.github.sophon.core.wiki.data.storeFingerprint
import io.github.sophon.wiki.data.WikiDB
import org.koin.core.scope.Scope

internal const val WIKI_DATABASE_NAME = "wiki.db"

/**
 * The platform's [WikiDB] driver, foreign keys on.
 * [databaseDirectory] null - the platform's default database location.
 */
internal expect fun Scope.createWikiSqlDriver(databaseDirectory: String?): SqlDriver

/**
 * There are no migrations - a database whose stored schema fingerprint differs from [WikiDB.Schema]'s
 * is deleted and created again; the next refresh downloads its data again.
 */
internal fun openFingerprintedDriver(
    open: () -> SqlDriver,
    delete: () -> Unit,
): SqlDriver {
    val expectedFingerprint = WikiDB.Schema.fingerprint()
    val driver = open()
    val storedFingerprint = driver.readStoredFingerprint()

    val openedDriver = when (storedFingerprint) {
        expectedFingerprint -> driver
        // the driver has just created the database
        null -> driver.also { it.storeFingerprint(expectedFingerprint) }
        else -> {
            Napier.i(tag = TAG) { "schema changed - recreating $WIKI_DATABASE_NAME" }
            driver.close()
            delete()
            open().also { recreatedDriver -> recreatedDriver.storeFingerprint(expectedFingerprint) }
        }
    }
    return openedDriver
}


private const val TAG = "WikiSqlDriver"
