package io.github.sophon.wikiSuperCombo

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val superComboModule = module {
    singleOf(::SuperComboWikiClientImpl).bind<SuperComboWikiClient>()
}