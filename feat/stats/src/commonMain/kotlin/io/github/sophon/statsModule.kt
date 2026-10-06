package io.github.sophon

import io.github.sophon.adapter.outbound.file.StatsFileAdapter
import io.github.sophon.app.outPort.DayReportPort
import io.github.sophon.app.outPort.MonthReportPort
import io.github.sophon.app.outPort.PrepareStoragePort
import io.github.sophon.app.service.ConfigureStatsService
import io.github.sophon.app.service.DayRolloverService
import io.github.sophon.app.service.DayRolloverServiceImpl
import io.github.sophon.app.service.GetCurrentReportService
import io.github.sophon.app.service.GetReportService
import io.github.sophon.app.service.RecordUsageService
import io.github.sophon.inPort.ConfigureStatsUseCase
import io.github.sophon.inPort.GetCurrentReportUseCase
import io.github.sophon.inPort.GetReportUseCase
import io.github.sophon.inPort.RecordUsageUseCase
import okio.FileSystem
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock

fun statsModule(directory: String) = module {
    single { StatsFeatureInfo }

    singleOf(::ConfigureStatsService).bind<ConfigureStatsUseCase>()
    singleOf(::RecordUsageService).bind<RecordUsageUseCase>()
    singleOf(::GetReportService).bind<GetReportUseCase>()
    singleOf(::GetCurrentReportService).bind<GetCurrentReportUseCase>()

    single<DayRolloverService> {
        DayRolloverServiceImpl(dayReportPort = get(), monthReportPort = get(), clock = Clock.System)
    }

    single { StatsFileAdapter(json = get(), fileSystem = FileSystem.SYSTEM, directory = directory) } withOptions {
        bind<PrepareStoragePort>()
        bind<DayReportPort>()
        bind<MonthReportPort>()
    }
}
