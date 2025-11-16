package io.github.sophon.fightingnerd.featureRegistry

import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.SupercomboWikiComposeFeature
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.superComboFeatureModule
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.WavuWikiComposeFeature
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.wavuWikiFeatureModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    includes(
        wavuWikiFeatureModule,
        superComboFeatureModule,
    )
    singleOf(::WavuWikiComposeFeature).bind<ComposeRegisteredFeature>()
    singleOf(::SupercomboWikiComposeFeature).bind<ComposeRegisteredFeature>()

    single<List<ComposeRegisteredFeature>>{ getAll() }

    singleOf(::FeatureRegistry)
    singleOf(::FeatureListLoaderImpl).bind<FeatureListLoader>()
}