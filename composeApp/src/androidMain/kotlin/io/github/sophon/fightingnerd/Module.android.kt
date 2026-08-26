package io.github.sophon.fightingnerd

import android.content.Context
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.data.fingerprint
import io.github.sophon.core.wiki.data.readStoredFingerprint
import io.github.sophon.core.wiki.data.storeFingerprint
import io.github.sophon.fightingnerd.core.domain.UrlOpener
import io.github.sophon.fightingnerd.core.domain.UrlOpenerAnd
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }

    singleOf(::UrlOpenerAnd).bind<UrlOpener>()

    single<Path> { androidContext().filesDir.toOkioPath() / "media" }

    WikiClientFeature.entries.forEach { feature ->
        single<SqlDriver>(named(feature.id)) { params ->
            val schema = params.get<SqlSchema<QueryResult.Value<Unit>>>()
            val ctx = androidContext()
            val dbName = "${feature.id}.db"
            openFingerprintedDriver(schema, ctx, dbName)
        }
    }
}

private fun openFingerprintedDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    ctx: Context,
    dbName: String,
): SqlDriver {
    val expected = schema.fingerprint()
    val wasFresh = ctx.getDatabasePath(dbName).exists().not()

    val driver = try {
        AndroidSqliteDriver(schema, ctx, dbName)
    } catch (t: Throwable) {
        ctx.deleteDatabase(dbName)
        AndroidSqliteDriver(schema, ctx, dbName)
    }

    if (wasFresh) {
        driver.storeFingerprint(expected)
        return driver
    }

    val stored = driver.readStoredFingerprint()
    if (stored == expected) return driver

    driver.close()
    ctx.deleteDatabase(dbName)
    val fresh = AndroidSqliteDriver(schema, ctx, dbName)
    fresh.storeFingerprint(expected)
    return fresh
}
