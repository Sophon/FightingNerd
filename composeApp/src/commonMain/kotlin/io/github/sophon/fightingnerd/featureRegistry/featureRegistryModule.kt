package io.github.sophon.fightingnerd.featureRegistry

import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.SuperComboComposeFeature
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.WavuComposeFeature
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    singleOf(::WavuComposeFeature).bind<ComposeRegisteredFeature>()
    singleOf(::SuperComboComposeFeature).bind<ComposeRegisteredFeature>()

    single<List<ComposeRegisteredFeature>> { getAll() }

    singleOf(::FeatureRegistry)
    singleOf(::FeatureListLoaderImpl).bind<FeatureListLoader>()

    // Initialize features after registry is created
    single {
        val registry = get<FeatureRegistry>()
        registry.apply {
            // Trigger initialization that calls registerGames
            GlobalScope.launch {
                initialize()
            }
        }
    }
}