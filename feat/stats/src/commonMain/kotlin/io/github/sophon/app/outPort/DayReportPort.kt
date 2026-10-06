package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

internal interface DayReportPort {
    suspend fun loadDay(): Result<DailyReport?, StatsError>
    suspend fun saveDay(dailyReport: DailyReport): EmptyResult<StatsError>
}
