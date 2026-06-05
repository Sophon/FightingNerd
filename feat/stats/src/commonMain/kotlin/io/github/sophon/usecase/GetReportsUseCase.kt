package io.github.sophon.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.integration.data.ReportRepo
import io.github.sophon.integration.model.DailyReport
import io.github.sophon.integration.model.StatsError
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExcludeFromCoverage("TODO: cover with test")
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
