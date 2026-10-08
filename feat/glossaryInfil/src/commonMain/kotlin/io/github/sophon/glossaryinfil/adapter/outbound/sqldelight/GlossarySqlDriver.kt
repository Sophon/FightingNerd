package io.github.sophon.glossaryinfil.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver
import io.github.aakira.napier.Napier
import io.github.sophon.core.sqldelight.fingerprint
import io.github.sophon.core.sqldelight.readStoredFingerprint
import io.github.sophon.core.sqldelight.storeFingerprint
import io.github.sophon.glossaryinfil.data.GlossaryDB

internal const val GLOSSARY_DATABASE_NAME = "glossary.db"

/**
 * The platform's [GlossaryDB] driver.
 * [databaseDirectory] null - the platform's default database location.
 */
internal expect fun createGlossarySqlDriver(databaseDirectory: String?): SqlDriver

/**
 * There are no migrations - a database whose stored schema fingerprint differs from [GlossaryDB.Schema]'s
 * is deleted and created again; the next refresh downloads its data again.
 */
internal fun openFingerprintedDriver(
    open: () -> SqlDriver,
    delete: () -> Unit,
): SqlDriver {
    val expectedFingerprint = GlossaryDB.Schema.fingerprint()
    val driver = open()
    val storedFingerprint = driver.readStoredFingerprint()

    val openedDriver = when (storedFingerprint) {
        expectedFingerprint -> driver
        // the driver has just created the database
        null -> driver.also { it.storeFingerprint(expectedFingerprint) }
        else -> {
            Napier.i(tag = TAG) { "schema changed - recreating $GLOSSARY_DATABASE_NAME" }
            driver.close()
            delete()
            open().also { recreatedDriver -> recreatedDriver.storeFingerprint(expectedFingerprint) }
        }
    }
    return openedDriver
}


private const val TAG = "GlossarySqlDriver"
