package io.github.sophon.integration.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.integration.model.DailyReport
import io.github.sophon.integration.model.StatsError

interface ReportRepo {
    suspend fun init(): EmptyResult<StatsError>
    suspend fun load(): Result<List<DailyReport>, StatsError>
    suspend fun save(dailyReportList: List<DailyReport>): EmptyResult<StatsError>
}
