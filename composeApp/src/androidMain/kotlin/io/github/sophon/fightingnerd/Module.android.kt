package io.github.sophon.fightingnerd

import io.github.sophon.fightingnerd.core.data.db.DatabaseDriverFactory
import io.github.sophon.fightingnerd.core.domain.UrlOpener
import io.github.sophon.fightingnerd.core.domain.UrlOpenerAnd
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }
    single { DatabaseDriverFactory(androidContext()) }

    singleOf(::UrlOpenerAnd).bind<UrlOpener>()
}
