package io.github.sophon.discord.featureRegistry

import io.github.sophon.discord.config.ConfigLoader
import io.github.sophon.discord.featureRegistry.core.CoreDiscordFeature
import io.github.sophon.discord.featureRegistry.core.GetBotFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.dreamCancel.DreamCancelWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.infilGlossary.InfilGlossaryDiscordFeature
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.discord.featureRegistry.wikiDustLoop.DustLoopWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.SuperComboWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiWavu.FileReaderJVM
import io.github.sophon.discord.featureRegistry.wikiWavu.WavuWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiXko.XkoWikiDiscordFeature
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.GetMovesUseCase
import io.github.sophon.discord.usecase.GetStancesUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    //region CORE
    singleOf(::GetBotFeatureInfoUseCase)
    //endregion

    //region Generic
    singleOf(::SyncWikiDataUseCase)
    singleOf(::GetMoveUseCase)
    singleOf(::GetCharacterUseCase)
    singleOf(::GetMoveUseCase)
    singleOf(::GetMovesUseCase)
    singleOf(::GetStancesUseCase)

    singleOf(::Scheduler)
    //endregion

    //region Wavu Wiki
    singleOf(::FileReaderJVM).bind<FileReader>()
    //endregion

    //region Infil glossary
    singleOf(::GetInfilFeatureInfoUseCase)
    singleOf(::StartGlossaryUseCase)
    singleOf(::SearchGlossaryUseCase)
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
    singleOf(::XkoWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::DreamCancelWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::DustLoopWikiDiscordFeature).bind<DiscordRegisteredFeature>()

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
                feature.registerGames(featureConfig.supportedGameList)
            }
        }

        features
    }
    //endregion
}