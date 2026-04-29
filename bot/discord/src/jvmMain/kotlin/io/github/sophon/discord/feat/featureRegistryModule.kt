package io.github.sophon.discord.feat

import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Config
import io.github.sophon.discord.domain.Scheduler
import io.github.sophon.discord.domain.Tracker
import io.github.sophon.discord.domain.TrackerImpl
import io.github.sophon.discord.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.admin.AdminDiscordFeature
import io.github.sophon.discord.feat.admin.usecase.BanUseCase
import io.github.sophon.discord.feat.admin.usecase.CreateRedirectButtonsUseCase
import io.github.sophon.discord.feat.admin.usecase.ProcessFeedbackUseCase
import io.github.sophon.discord.feat.admin.usecase.ReplyToFeedbackUseCase
import io.github.sophon.discord.feat.admin.usecase.StartAdminToolsUseCase
import io.github.sophon.discord.feat.admin.usecase.UnbanUseCase
import io.github.sophon.discord.feat.bot.BotFeature
import io.github.sophon.discord.feat.bot.DiscordBot
import io.github.sophon.discord.feat.bot.DiscordBotImpl
import io.github.sophon.discord.feat.bot.usecase.CreateEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.CreateErrorEmbedBuilderUseCase
import io.github.sophon.discord.feat.bot.usecase.CreateFeedbackEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.CreateJoinEmbedButtonUseCase
import io.github.sophon.discord.feat.bot.usecase.CreateMutableEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.CreatePlainMessageUseCase
import io.github.sophon.discord.feat.bot.usecase.CreateReplyEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.feat.bot.usecase.HandleButtonInteractionUseCase
import io.github.sophon.discord.feat.bot.usecase.PostDailyReportEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.ResultToEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.discord.feat.config.ConfigLoader
import io.github.sophon.discord.feat.config.FeatureRegistry
import io.github.sophon.discord.feat.dreamCancel.DreamCancelWikiDiscordFeature
import io.github.sophon.discord.feat.ewgf.EwgfDiscordFeature
import io.github.sophon.discord.feat.ewgf.usecase.GetRecentMatchesUseCase
import io.github.sophon.discord.feat.ewgf.usecase.ParseQueryIntoOperationUseCase
import io.github.sophon.discord.feat.ewgf.usecase.RegisterPlayerUseCase
import io.github.sophon.discord.feat.ewgf.usecase.UnregisterPlayerUseCase
import io.github.sophon.discord.feat.ewgf.usecase.UpdatePlayerUseCase
import io.github.sophon.discord.feat.infilGlossary.InfilGlossaryDiscordFeature
import io.github.sophon.discord.feat.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.discord.feat.wikiDustLoop.DustLoopWikiDiscordFeature
import io.github.sophon.discord.feat.wikiDustLoop.FetchDustLoopInvincibleMovesUseCase
import io.github.sophon.discord.feat.wikiDustLoop.usecase.CreateCharacterEmbedUseCase
import io.github.sophon.discord.feat.wikiDustLoop.usecase.CreateMoveEmbedUseCase
import io.github.sophon.discord.feat.wikiMizuumi.CreateMizuumiInvEmbedUseCase
import io.github.sophon.discord.feat.wikiMizuumi.MizuumiWikiDiscordFeature
import io.github.sophon.discord.feat.wikiSuperCombo.SuperComboWikiDiscordFeature
import io.github.sophon.discord.feat.wikiWavu.FileReaderJVM
import io.github.sophon.discord.feat.wikiWavu.WavuWikiDiscordFeature
import io.github.sophon.discord.feat.wikiWavu.usecase.GetStancesUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.SearchStringFollowupsUseCase
import io.github.sophon.discord.feat.wikiXko.XkoWikiDiscordFeature
import io.github.sophon.discord.feat.core.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
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
    singleOf(::CreateRedirectButtonsUseCase)
    //endregion

    //region CORE
    singleOf(::DiscordBotImpl).bind<DiscordBot>()

    single {
        TrackerImpl(
            statsFeatureInfo = get(),
            statsChannelId = get<Config>().statsConfig?.statsChannelIdList?.firstOrNull() ?: "",
            scheduler = get(),
            scope = get(),
            statsTracker = get(),
        )
    }.bind<Tracker>()

    singleOf(::GetBotFeatureInfoUseCase)
    singleOf(::CreateJoinEmbedButtonUseCase)
    singleOf(::RouteCommandToFeatureUseCase)
    singleOf(::CreateErrorEmbedBuilderUseCase)
    singleOf(::CreatePlainMessageUseCase)
    singleOf(::CreateEmbedUseCase)
    singleOf(::CreateFeedbackEmbedUseCase)
    singleOf(::CreateReplyEmbedUseCase)
    singleOf(::ResultToEmbedUseCase)
    singleOf(::CreateMutableEmbedUseCase)
    singleOf(::HandleButtonInteractionUseCase)
    singleOf(::PostDailyReportEmbedUseCase)
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
    singleOf(::FetchMoveInWikisUseCase)

    singleOf(::Scheduler)
    //endregion

    //region DustLoop
    singleOf(::CreateMoveEmbedUseCase)
    singleOf(::CreateCharacterEmbedUseCase)
    singleOf(::FetchDustLoopInvincibleMovesUseCase)
    //endregion

    //region Wavu
    singleOf(::FileReaderJVM).bind<FileReader>()
    singleOf(::SearchStringFollowupsUseCase)
    //endregion

    //region Mizuumi
    singleOf(::CreateMizuumiInvEmbedUseCase)
    //endregion

    //region Infil glossary
    singleOf(::GetInfilFeatureInfoUseCase)
    singleOf(::StartGlossaryUseCase)
    singleOf(::SearchGlossaryUseCase)
    //endregion

    //region EWGF
    singleOf(::ParseQueryIntoOperationUseCase)
    singleOf(::RegisterPlayerUseCase)
    singleOf(::GetRecentMatchesUseCase)
    singleOf(::UpdatePlayerUseCase)
    singleOf(::UnregisterPlayerUseCase)
    //endregion

    //region CONFIG
    singleOf(::ConfigLoader)
    single {
        when (val result = get<ConfigLoader>().loadConfig()) {
            is Result.Success -> result.data
            is Result.Error -> throw IllegalStateException("Failed to load config: ${result.error}")
        }
    }
    single<Config.AdminConfig> { get<Config>().adminConfig!! }
    //endregion

    //region FEATURES SETUP
    single {
        FeatureRegistry(
            features = getAll(),
            coreFeature = get<BotFeature>(),
        )
    }

    single {
        AdminDiscordFeature(
            adminFeatureInfo = get(),
            adminConfig = get<Config.AdminConfig>(),
            startAdminToolsUseCase = get(),
            processFeedbackUseCase = get(),
            replyToFeedbackUseCase = get(),
            createRedirectButtonsUseCase = get(),
            banUseCase = get(),
            unbanUseCase = get(),
            scheduler = get(),
            scope = get(),
        )
    }

    singleOf(::BotFeature).bind<DiscordRegisteredFeature>()
    singleOf(::InfilGlossaryDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::WavuWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::SuperComboWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::XkoWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::DreamCancelWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::DustLoopWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::MizuumiWikiDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::EwgfDiscordFeature).bind<DiscordRegisteredFeature>()

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