package io.github.sophon.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.data.ReportRepo
import io.github.sophon.domain.StatsError
import io.github.sophon.domain.model.Command
import io.github.sophon.domain.model.DailyReport
import io.github.sophon.util.today
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class SaveTodaysReport(
    private val repo: ReportRepo,
) {
    suspend fun invoke(
        recordLength: Duration,
        cache: Map<Command, Long>,
    ): Result<DailyReport, StatsError> {
        return repo.load()
            .flatMap { reportList ->
                val today = today()
                val report = DailyReport(
                    date = today,
                    commandMap = cache,
                )
                val cutoffDate = Clock.System.now()
                    .minus(recordLength)
                    .toLocalDateTime(TimeZone.UTC)
                    .date
                val updatedReportList = reportList.filter { it.date >= cutoffDate } + report

                repo.save(updatedReportList)
                    .map { report }
            }
    }
}
