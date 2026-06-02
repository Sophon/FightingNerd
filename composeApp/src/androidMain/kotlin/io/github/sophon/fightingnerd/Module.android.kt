package io.github.sophon.fightingnerd

import io.github.sophon.fightingnerd.infrastructure.createDataStore
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }
}
