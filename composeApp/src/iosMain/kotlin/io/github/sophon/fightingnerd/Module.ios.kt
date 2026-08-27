package io.github.sophon.fightingnerd

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseFileContext
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.data.fingerprint
import io.github.sophon.core.wiki.data.readStoredFingerprint
import io.github.sophon.core.wiki.data.storeFingerprint
import io.github.sophon.fightingnerd.core.domain.UrlOpener
import io.github.sophon.fightingnerd.core.domain.UrlOpenerIos
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

internal actual val platformModule = module {
    single { createDataStore() }
    singleOf(::UrlOpenerIos).bind<UrlOpener>()

    single<Path> {
        val dirs = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            true,
        )
        val supportPath = dirs.first() as String
        val baseDir = supportPath.toPath() / "media"
        baseDir
    }

    WikiClientFeature.entries.forEach { feature ->
        single<SqlDriver>(named(feature.id)) { params ->
            val schema = params.get<SqlSchema<QueryResult.Value<Unit>>>()
            val dbName = "${feature.id}.db"
            openFingerprintedDriver(schema, dbName)
        }
    }
}

private fun openFingerprintedDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    dbName: String,
): SqlDriver {
    val expected = schema.fingerprint()
    val dbPath = DatabaseFileContext.databasePath(dbName, null)
    val wasFresh = NSFileManager.defaultManager.fileExistsAtPath(dbPath).not()

    val driver = try {
        NativeSqliteDriver(schema, dbName)
    } catch (t: Throwable) {
        DatabaseFileContext.deleteDatabase(dbName)
        NativeSqliteDriver(schema, dbName)
    }

    if (wasFresh) {
        driver.storeFingerprint(expected)
        return driver
    }

    val stored = driver.readStoredFingerprint()
    if (stored == expected) return driver

    driver.close()
    DatabaseFileContext.deleteDatabase(dbName)
    val fresh = NativeSqliteDriver(schema, dbName)
    fresh.storeFingerprint(expected)
    return fresh
}
