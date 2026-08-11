package io.github.sophon.fightingnerd

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.fightingnerd.core.domain.UrlOpener
import io.github.sophon.fightingnerd.core.domain.UrlOpenerAnd
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }

    singleOf(::UrlOpenerAnd).bind<UrlOpener>()

    WikiClientFeature.entries.forEach { feature ->
        single<SqlDriver>(named(feature.id)) { params ->
            AndroidSqliteDriver(
                schema = params.get<SqlSchema<QueryResult.Value<Unit>>>(),
                context = androidContext(),
                name = "${feature.id}.db",
            )
        }
    }
}
