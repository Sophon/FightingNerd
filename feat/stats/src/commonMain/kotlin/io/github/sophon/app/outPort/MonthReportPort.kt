package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

internal interface MonthReportPort {
    suspend fun loadMonth(): Result<List<DailyReport>, StatsError>
    suspend fun saveMonth(dailyReportList: List<DailyReport>): EmptyResult<StatsError>
}
