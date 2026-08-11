package io.github.sophon.fightingnerd.core

import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.core.data.store.PreferenceRepoImpl
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.usecase.OpenUrlUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun coreModule() = module {
    singleOf(::PreferenceRepoImpl).bind<PreferenceRepo>()

    singleOf(::OpenUrlUseCase)

    singleOf(::OverlayService)
}
