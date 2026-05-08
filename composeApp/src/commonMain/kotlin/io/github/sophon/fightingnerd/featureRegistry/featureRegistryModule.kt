package io.github.sophon.fightingnerd.featureRegistry

import io.github.sophon.fightingnerd.feat.config.model.Module
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.SuperComboComposeFeature
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.superComboComposeModule
import io.github.sophon.fightingnerd.featureRegistry.usecase.FetchCharacterListUseCase
import io.github.sophon.fightingnerd.featureRegistry.usecase.SyncDataIfOldUseCase
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.WavuComposeFeature
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.wavuComposeModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    includes(
        wavuComposeModule,
        superComboComposeModule,
    )

    singleOf(::SyncDataIfOldUseCase)
    singleOf(::FetchCharacterListUseCase)

    // 1. Register individual features
    singleOf(::WavuComposeFeature).bind<Module>()
    singleOf(::SuperComboComposeFeature).bind<Module>()

    // 2. Collect features into a list
    single<List<Module>> {
        getAll()
    }

    // 3. Create loader
    singleOf(::FeatureListLoaderImpl).bind<FeatureListLoader>()

    // 4. Create registry with the list
    single {
        FeatureRegistry(
            featureListLoader = get(),
            fullFeatureList = get()  // Explicit dependency
        ).apply {
            // 5. Initialize after creation
            GlobalScope.launch {
                initialize()
            }
        }
    }
}