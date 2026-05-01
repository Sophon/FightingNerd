package io.github.sophon.integration

import io.github.sophon.domain.StatsFeatureInfo
import io.github.sophon.domain.StatsTrackerImpl
import io.github.sophon.usecase.GetReportsUseCase
import io.github.sophon.usecase.InitRepoUseCase
import io.github.sophon.usecase.RecordUseCase
import io.github.sophon.usecase.ResetCacheUseCase
import io.github.sophon.usecase.SaveTodaysReport
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun statsModule() = module {
    single { StatsFeatureInfo }

    singleOf(::StatsTrackerImpl).bind(StatsTracker::class)

    singleOf(::InitRepoUseCase)
    singleOf(::RecordUseCase)
    singleOf(::SaveTodaysReport)
    singleOf(::ResetCacheUseCase)
    singleOf(::GetReportsUseCase)
}
