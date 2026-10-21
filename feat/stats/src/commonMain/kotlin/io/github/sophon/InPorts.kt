package io.github.sophon

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.Command
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

interface ConfigureStatsUseCase {
    suspend operator fun invoke(): EmptyResult<StatsError>
}

interface GetCurrentReportUseCase {
    suspend operator fun invoke(): Result<DailyReport, StatsError>
}

interface GetReportUseCase {
    suspend operator fun invoke(): Result<List<DailyReport>, StatsError>
}

interface RecordUsageUseCase {
    suspend operator fun invoke(command: Command): EmptyResult<StatsError>
}
