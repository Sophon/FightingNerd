package io.github.sophon.integration

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.integration.model.Command
import io.github.sophon.integration.model.DailyReport
import io.github.sophon.integration.model.StatsError
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface StatsTracker {
    suspend fun init(): EmptyResult<StatsError>
    suspend fun record(command: Command): EmptyResult<StatsError>
    suspend fun finalizeDay(recordLength: Duration = 30.days): Result<DailyReport, StatsError>
    suspend fun resetCached(): EmptyResult<StatsError>
    suspend fun getReports(duration: Duration = 30.days): Result<List<DailyReport>, StatsError>
}

