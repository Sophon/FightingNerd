package io.github.sophon.fightingnerd

import io.github.sophon.fightingnerd.core.data.DatabaseDriverFactory
import io.github.sophon.fightingnerd.core.domain.UrlOpener
import io.github.sophon.fightingnerd.core.domain.UrlOpenerIos
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }
    single { DatabaseDriverFactory() }
    singleOf(::UrlOpenerIos).bind<UrlOpener>()
}
