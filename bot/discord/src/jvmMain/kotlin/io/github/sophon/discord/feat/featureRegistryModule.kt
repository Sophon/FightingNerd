package io.github.sophon.discord.feat

import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesWithinRangeUseCase
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
}