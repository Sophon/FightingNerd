package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.bottomBar.ui.BottomBarVM
import io.github.sophon.fightingnerd.feat.moduleList.ui.ModuleListVM
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun featureModule() = module {
    viewModelOf(::BottomBarVM)
    viewModelOf(::ModuleListVM)
}