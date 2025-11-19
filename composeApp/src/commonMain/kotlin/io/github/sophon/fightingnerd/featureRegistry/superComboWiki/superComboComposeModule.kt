package io.github.sophon.fightingnerd.featureRegistry.superComboWiki

import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui.SuperComboHomeVM
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val superComboComposeModule = module {
    viewModelOf(::SuperComboHomeVM)
}