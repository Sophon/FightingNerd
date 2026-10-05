package io.github.sophon.discord.feat

import io.github.sophon.discord.feat.bot.usecase.PostDailyReportEmbedUseCase
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesWithinRangeUseCase
import io.github.sophon.discord.feat.ewgf.usecase.GetRecentMatchesUseCase
import io.github.sophon.discord.feat.ewgf.usecase.ParseQueryIntoOperationUseCase
import io.github.sophon.discord.feat.ewgf.usecase.RegisterPlayerUseCase
import io.github.sophon.discord.feat.ewgf.usecase.UnregisterPlayerUseCase
import io.github.sophon.discord.feat.ewgf.usecase.UpdatePlayerUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.StartGlossaryUseCase
import org.koin.core.module.dsl.singleOf
//import org.koin.dsl.bind
import org.koin.dsl.module

internal val featureRegistryModule = module {
    //region CORE
//    single {
//        TrackerImpl(
//            statsFeatureInfo = get(),
//            statsChannelId = get<Config>().statsConfig?.statsChannelIdList?.firstOrNull() ?: "",
//            scheduler = get(),
//            scope = get(),
//            statsTracker = get(),
//        )
//    }.bind<Tracker>()

    singleOf(::GetBotFeatureInfoUseCase)
    singleOf(::PostDailyReportEmbedUseCase)
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

}