package io.github.sophon.discord.featureRegistry

import io.github.sophon.discord.config.ConfigLoader
import io.github.sophon.discord.featureRegistry.infilGlossary.InfilGlossaryDiscordFeature
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.FileReaderJVM
import io.github.sophon.discord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.discord.featureRegistry.wikiWavu.WavuWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.GetHeatMovesUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.GetHomingMovesUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.GetPowerCrushMovesUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.SearchFrameDataUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.SyncDataUseCase
import io.github.sophon.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    //region Wavu Wiki
    singleOf(::SyncDataUseCase)
    singleOf(::SearchFrameDataUseCase)
    singleOf(::GetPowerCrushMovesUseCase)
    singleOf(::GetHeatMovesUseCase)
    singleOf(::GetHomingMovesUseCase)

    singleOf(::Scheduler)
    singleOf(::FileReaderJVM).bind<FileReader>()
    //endregion

    //region Infil glossary
    singleOf(::StartGlossaryUseCase)
    singleOf(::SearchGlossaryUseCase)
    //endregion

    //region FEATURES
    single { FeatureRegistry(getAll()) }

    singleOf(::WavuWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::InfilGlossaryDiscordFeature).bind<DiscordRegisteredFeature>()

    singleOf(::ConfigLoader)
    single<List<DiscordRegisteredFeature>> {
        val config = get<ConfigLoader>().loadConfig()
        val registry = get<FeatureRegistry>()
        val enabledFeatures = config.featureList
            .filter { it.isEnabled }
            .map { it.name }
        registry.getFeatures(enabledFeatures)
    }
    //endregion
}