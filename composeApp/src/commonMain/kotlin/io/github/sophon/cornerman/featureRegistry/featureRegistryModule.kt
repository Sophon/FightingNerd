package io.github.sophon.cornerman.featureRegistry

import io.github.sophon.cornerman.featureRegistry.superComboWiki.SupercomboWikiComposeFeature
import io.github.sophon.cornerman.featureRegistry.superComboWiki.superComboFeatureModule
import io.github.sophon.cornerman.featureRegistry.wavuWiki.WavuWikiComposeFeature
import io.github.sophon.cornerman.featureRegistry.wavuWiki.wavuWikiFeatureModule
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