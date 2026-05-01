package io.github.sophon.integration

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.model.StatsError
import io.github.sophon.domain.model.Command
import io.github.sophon.domain.model.DailyReport
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface StatsTracker {
    suspend fun init(): EmptyResult<StatsError>
    suspend fun record(command: Command): EmptyResult<StatsError>
    suspend fun finalizeDay(recordLength: Duration = 30.days): Result<DailyReport, StatsError>
    suspend fun resetCached(): EmptyResult<StatsError>
    suspend fun getReports(duration: Duration = 30.days): Result<List<DailyReport>, StatsError>
}

