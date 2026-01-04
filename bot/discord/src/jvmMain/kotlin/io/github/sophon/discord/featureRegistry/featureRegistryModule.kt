package io.github.sophon.discord.featureRegistry

import io.github.sophon.core.feature.Config
import io.github.sophon.discord.config.ConfigLoader
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.domain.Scheduler
import io.github.sophon.discord.featureRegistry.admin.AdminDiscordFeature
import io.github.sophon.discord.featureRegistry.admin.usecase.BanUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.ProcessFeedbackUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.ReplyToFeedbackUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.StartAdminToolsUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.UnbanUseCase
import io.github.sophon.discord.featureRegistry.core.CoreDiscordFeature
import io.github.sophon.discord.featureRegistry.core.GetBotFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.dreamCancel.DreamCancelWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.infilGlossary.InfilGlossaryDiscordFeature
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.discord.featureRegistry.wikiDustLoop.DustLoopWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiMizuumi.MizuumiWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiSuperCombo.SuperComboWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiWavu.FileReaderJVM
import io.github.sophon.discord.featureRegistry.wikiWavu.WavuWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiXko.XkoWikiDiscordFeature
import io.github.sophon.discord.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.GetCharactersUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.GetMovesUseCase
import io.github.sophon.discord.usecase.GetStancesUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    //region ADMIN
    singleOf(::StartAdminToolsUseCase)
    singleOf(::ProcessFeedbackUseCase)
    singleOf(::ReplyToFeedbackUseCase)
    singleOf(::BanUseCase)
    singleOf(::UnbanUseCase)
    //endregion

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
    singleOf(::GetCharactersUseCase)
    singleOf(::CreateCharacterAliasesEmbedUseCase)

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

    singleOf(::ConfigLoader)
    single { get<ConfigLoader>().loadConfig() }
    single<Config.AdminConfig> { get<Config>().adminConfig!! }

    //region FEATURES SETUP
    single {
        FeatureRegistry(
            features = getAll(),
            coreFeature = get<CoreDiscordFeature>(),
        )
    }

    single {
        AdminDiscordFeature(
            adminFeatureInfo = get(),
            adminConfig = get<Config.AdminConfig>(),
            startAdminToolsUseCase = get(),
            processFeedbackUseCase = get(),
            replyToFeedbackUseCase = get(),
            banUseCase = get(),
            unbanUseCase = get(),
            scheduler = get(),
            scope = get(),
        )
    }

    singleOf(::CoreDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::InfilGlossaryDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::WavuWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::SuperComboWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::XkoWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::DreamCancelWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::DustLoopWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::MizuumiWikiDiscordFeature).bind<DiscordRegisteredFeature>()

    single<List<DiscordRegisteredFeature>> {
        val config = get<Config>()
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
        val adminFeature: AdminDiscordFeature = get()

        features + adminFeature
    }
    //endregion
}