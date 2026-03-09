package io.github.sophon

import io.github.sophon.domain.StatsFeatureInfo
import io.github.sophon.domain.usecase.GetReportsUseCase
import io.github.sophon.domain.usecase.RecordUseCase
import io.github.sophon.domain.usecase.ResetCacheUseCase
import io.github.sophon.domain.usecase.SaveTodaysReport
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun statsModule() = module {
    single { StatsFeatureInfo }

    singleOf(::StatsTrackerImpl).bind(StatsTracker::class)

    singleOf(::RecordUseCase)
    singleOf(::SaveTodaysReport)
    singleOf(::ResetCacheUseCase)
    singleOf(::GetReportsUseCase)
}
