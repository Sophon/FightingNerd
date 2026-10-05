package io.github.sophon.app

import io.github.sophon.app.outPort.DayReportPort
import io.github.sophon.app.outPort.MonthReportPort
import io.github.sophon.app.util.todayUtc
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.time.Clock

/**
 * Archives day.json into month.json lazily - the first access on a new UTC day does it,
 * so a bot that was down over midnight still archives the day it missed.
 */
internal class DayRollover(
    private val dayReportPort: DayReportPort,
    private val monthReportPort: MonthReportPort,
    private val clock: Clock,
) {
    private val mutex = Mutex()

    /**
     * Serialized, so concurrent records can't lose a count or archive the same day twice.
     */
    suspend fun <T> withCurrentReport(
        block: suspend (DailyReport) -> Result<T, StatsError>,
    ): Result<T, StatsError> {
        mutex.withLock {
            val today = clock.todayUtc()
            val result = dayReportPort.loadDay()
                .flatMap { storedReport ->
                    when {
                        storedReport == null -> Result.Success(DailyReport(date = today, commandMap = emptyMap()))
                        storedReport.date == today -> Result.Success(storedReport)
                        else -> archive(finishedReport = storedReport, today = today)
                    }
                }
                .flatMap { currentReport -> block(currentReport) }
            return result
        }
    }


    /**
     * Month is saved before day, and replaces any entry with the same date - if saving day fails,
     * the next access archives the same report again without duplicating it.
     */
    private suspend fun archive(
        finishedReport: DailyReport,
        today: LocalDate,
    ): Result<DailyReport, StatsError> {
        val cutoffDate = today.minus(MONTH_LENGTH_DAYS, DateTimeUnit.DAY)
        val freshReport = DailyReport(date = today, commandMap = emptyMap())

        val result = monthReportPort.loadMonth()
            .flatMap { monthReportList ->
                val updatedMonthReportList = (monthReportList.filterNot { it.date == finishedReport.date } + finishedReport)
                    .filter { report -> report.date >= cutoffDate }
                    .sortedBy { report -> report.date }
                monthReportPort.saveMonth(updatedMonthReportList)
            }
            .flatMap { dayReportPort.saveDay(freshReport) }
            .map { freshReport }
        return result
    }
}
