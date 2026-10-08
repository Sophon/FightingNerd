package io.github.sophon.fightingnerd.core

import io.github.aakira.napier.Napier
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.OverlayService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun coreModule() = module {
    single {
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Default +
                    CoroutineExceptionHandler { _, throwable ->
                        Napier.e(tag = "WikiClient") { "Unhandled exception: $throwable" }
                    }
        )
    }
}
