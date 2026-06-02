package io.github.sophon.fightingnerd

import io.github.sophon.fightingnerd.core.data.DatabaseDriverFactory
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }
    single { DatabaseDriverFactory(androidContext()) }
}
