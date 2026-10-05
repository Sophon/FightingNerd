package io.github.sophon.discord.feat

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.discord.adapter.inbound.kord.DiscordBot
import io.github.sophon.discord.adapter.inbound.kord.DiscordBotImpl
import io.github.sophon.discord.adapter.inbound.kord.KordResponder
import io.github.sophon.discord.feat.bot.BotFeature
import io.github.sophon.discord.feat.bot.usecase.CreateFeedbackEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.CreateJoinEmbedButtonUseCase
import io.github.sophon.discord.feat.bot.usecase.CreatePromoEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.CreateReplyEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.PostDailyReportEmbedUseCase
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.config.BotFeatureRepoImpl
import io.github.sophon.discord.feat.config.ConfigLoader
import io.github.sophon.discord.feat.config.FeatureRegistry
import io.github.sophon.discord.feat.config.usecase.BindToDiscordFeaturesUseCase
import io.github.sophon.discord.feat.config.usecase.LoadConfigurationUseCase
import io.github.sophon.discord.feat.core.data.InMemoryCharacterListDB
import io.github.sophon.discord.feat.core.data.InMemoryMoveListDB
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.Tracker
import io.github.sophon.discord.feat.core.domain.TrackerImpl
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesWithinRangeUseCase
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    //region CORE
    singleOf(::DiscordBotImpl).bind<DiscordBot>()
    singleOf(::KordResponder)

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
    singleOf(::CreateFeedbackEmbedUseCase)
    singleOf(::CreateReplyEmbedUseCase)
    singleOf(::PostDailyReportEmbedUseCase)
    singleOf(::CreatePromoEmbedUseCase)
    //endregion

    //region Generic
    singleOf(::GetMovesWithinRangeUseCase)

    singleOf(::Scheduler)
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
    singleOf(::BotFeatureRepoImpl).bind<BotFeatureRepo>()

    singleOf(::LoadConfigurationUseCase)
    single {
        BindToDiscordFeaturesUseCase(
            allRegisteredFeatures = getAll(),
            featureRepo = get(),
        )
    }

    single {
        when (val result = get<ConfigLoader>().loadConfig()) {
            is Result.Success -> result.data
            is Result.Error -> error("Failed to load config: ${result.error}")
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

    singleOf(::BotFeature).bind<DiscordRegisteredFeature>()
    singleOf(::InfilGlossaryDiscordFeature).bind<DiscordRegisteredFeature>()
    singleOf(::EwgfDiscordFeature).bind<DiscordRegisteredFeature>()

    single<(Game) -> Pair<CharacterListDB, MoveListDB>> {
        return@single  { game ->
            val moveDB = InMemoryMoveListDB(game)
            val characterDB = InMemoryCharacterListDB()

            characterDB to moveDB
        }
    }
    //endregion
}