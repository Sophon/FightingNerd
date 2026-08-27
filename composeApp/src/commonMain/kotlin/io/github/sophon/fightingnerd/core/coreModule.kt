package io.github.sophon.fightingnerd.core

import io.github.aakira.napier.Napier
import io.github.sophon.fightingnerd.core.data.MediaRepo
import io.github.sophon.fightingnerd.core.data.MediaRepoImpl
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.core.data.store.PreferenceRepoImpl
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.usecase.OpenUrlUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import okio.SYSTEM
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
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

    single<MediaRepo> {
        MediaRepoImpl(
            fs = FileSystem.SYSTEM,
            baseDir = get(),
            http = get(),
            store = get(),
        )
    }

    singleOf(::PreferenceRepoImpl).bind<PreferenceRepo>()

    singleOf(::OpenUrlUseCase)

    singleOf(::OverlayService)
}
