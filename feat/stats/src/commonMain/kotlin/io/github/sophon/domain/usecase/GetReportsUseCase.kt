package io.github.sophon.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.data.ReportRepo
import io.github.sophon.domain.StatsError
import io.github.sophon.domain.model.DailyReport
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.filter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class GetReportsUseCase(
    private val repo: ReportRepo,
) {
    suspend fun invoke(
        duration: Duration
    ): Result<List<DailyReport>, StatsError> {
        return repo.load().map { recordList ->
            val cutoffDate = Clock.System.now()
                .minus(duration)
                .toLocalDateTime(TimeZone.UTC)
                .date
            recordList.filter { report ->
                report.date >= cutoffDate
            }
        }
    }
}
