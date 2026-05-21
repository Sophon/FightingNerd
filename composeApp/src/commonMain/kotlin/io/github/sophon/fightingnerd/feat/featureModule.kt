package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.bottomBar.ui.BottomBarVM
import io.github.sophon.fightingnerd.feat.module.ModuleRepo
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun featureModule() = module {
    viewModelOf(::BottomBarVM)

    singleOf(::LoadConfigUseCase)
    singleOf(::WikiClientFactory)

    singleOf(::ModuleRepo)
}
