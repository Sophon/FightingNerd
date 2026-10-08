package io.github.sophon.fightingnerd

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseFileContext
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.sqldelight.fingerprint
import io.github.sophon.core.sqldelight.readStoredFingerprint
import io.github.sophon.core.sqldelight.storeFingerprint
import io.github.sophon.fightingnerd.core.domain.UrlOpener
import io.github.sophon.fightingnerd.core.domain.UrlOpenerIos
import io.github.sophon.fightingnerd.app.outPort.ReviewPort
import io.github.sophon.fightingnerd.adapter.outbound.review.ReviewAdapter
import io.github.sophon.fightingnerd.adapter.outbound.scheduler.BGTaskScheduler
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import io.github.sophon.fightingnerd.app.outPort.SharePort
import io.github.sophon.fightingnerd.adapter.outbound.share.ShareAdapter
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

internal actual val platformModule = module {
    single<DataStore<Preferences>> {
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                @OptIn(ExperimentalForeignApi::class)
                val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
                val path = (requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME").toPath()
                path
            }
        )
        dataStore
    }
    singleOf(::UrlOpenerIos).bind<UrlOpener>()
    singleOf(::BGTaskScheduler).bind<SchedulerPort>()
    singleOf(::ShareAdapter).bind<SharePort>()
    singleOf(::ReviewAdapter).bind<ReviewPort>()


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


// Legacy name - existing installs already keep their preferences in this file
private const val DATA_STORE_FILE_NAME = "wavu_preferences.preferences_pb"
