package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.bottomBar.ui.BottomBarVM
import io.github.sophon.fightingnerd.feat.config.ModuleRegistry
import io.github.sophon.fightingnerd.feat.config.usecase.LoadConfigUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun featureModule() = module {
    viewModelOf(::BottomBarVM)

    singleOf(::LoadConfigUseCase)
    //singleOf(::WavuWikiModule).bind<Module>()
    //singleOf(::SuperComboWikiModule).bind<Module>()
    //etc
    single { ModuleRegistry(loadConfigUseCase = get(), availableModules = getAll()) }
}
