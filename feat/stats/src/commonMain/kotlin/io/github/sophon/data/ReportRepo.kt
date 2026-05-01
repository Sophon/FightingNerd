package io.github.sophon.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.model.DailyReport
import io.github.sophon.domain.model.StatsError

interface ReportRepo {
    suspend fun init(): EmptyResult<StatsError>
    suspend fun load(): Result<List<DailyReport>, StatsError>
    suspend fun save(dailyReportList: List<DailyReport>): EmptyResult<StatsError>
}
