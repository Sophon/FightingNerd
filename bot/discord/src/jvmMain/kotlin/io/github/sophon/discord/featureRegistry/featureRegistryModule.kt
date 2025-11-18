package io.github.sophon.discord.featureRegistry

import io.github.sophon.discord.config.ConfigLoader
import io.github.sophon.discord.featureRegistry.core.CoreDiscordFeature
import io.github.sophon.discord.featureRegistry.core.GetBotFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.InfilGlossaryDiscordFeature
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.SuperComboWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.GetMoveUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.GetSuperComboFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.SearchCharacterDataUseCase
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase.SyncSuperComboDataUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.FileReaderJVM
import io.github.sophon.discord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.discord.featureRegistry.wikiWavu.WavuWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.GetHeatMovesUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.GetHomingMovesUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.GetPowerCrushMovesUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.GetWavuFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.SearchFrameDataUseCase
import io.github.sophon.discord.featureRegistry.wikiWavu.usecase.SyncDataUseCase
import io.github.sophon.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    //region CORE
    singleOf(::GetBotFeatureInfoUseCase)
    //endregion

    //region Wavu Wiki
    singleOf(::GetWavuFeatureInfoUseCase)
    singleOf(::SyncDataUseCase)
    singleOf(::SearchFrameDataUseCase)
    singleOf(::GetPowerCrushMovesUseCase)
    singleOf(::GetHeatMovesUseCase)
    singleOf(::GetHomingMovesUseCase)

    singleOf(::Scheduler)
    singleOf(::FileReaderJVM).bind<FileReader>()
    //endregion

    //region Infil glossary
    singleOf(::GetInfilFeatureInfoUseCase)
    singleOf(::StartGlossaryUseCase)
    singleOf(::SearchGlossaryUseCase)
    //endregion

    //region SuperCombo Wiki
    singleOf(::GetSuperComboFeatureInfoUseCase)
    singleOf(::SyncSuperComboDataUseCase)
    singleOf(::SearchCharacterDataUseCase)
    singleOf(::GetMoveUseCase)
    //endregion

    //region FEATURES SETUP
    single {
        FeatureRegistry(
            features = getAll(),
            coreFeature = get<CoreDiscordFeature>(),
        )
    }

    singleOf(::CoreDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::InfilGlossaryDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::WavuWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::SuperComboWikiDiscordFeature).bind<DiscordRegisteredFeature>()

    singleOf(::ConfigLoader)
    single<List<DiscordRegisteredFeature>> {
        val config = get<ConfigLoader>().loadConfig()
        val registry = get<FeatureRegistry>()
        val enabledFeatures = config.featureList
            .filter { it.isEnabled }
            .map { it.name }
        val features = registry.getFeatures(enabledFeatures)

        features.forEach { feature ->
            val featureConfig = config.featureList
                .find { it.name == feature.featureInfo.name }

            if (featureConfig != null) {
                feature.registerGames(featureConfig.supportedGames)
            }
        }

        features
    }
    //endregion
}