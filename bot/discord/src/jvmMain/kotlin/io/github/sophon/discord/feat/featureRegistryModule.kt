package io.github.sophon.discord.feat

import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesWithinRangeUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.feat.infilGlossary.usecase.StartGlossaryUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val featureRegistryModule = module {
    //region CORE
    singleOf(::GetBotFeatureInfoUseCase)
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

}