package io.github.sophon.app.service

import io.github.sophon.app.MONTH_LENGTH_DAYS
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

internal interface DayRolloverService {
    suspend fun <T> withCurrentReport(
        block: suspend (DailyReport) -> Result<T, StatsError>,
    ): Result<T, StatsError>
}


internal class DayRolloverServiceImpl(
    private val dayReportPort: DayReportPort,
    private val monthReportPort: MonthReportPort,
    private val clock: Clock,
) : DayRolloverService {
    private val mutex = Mutex()

    override suspend fun <T> withCurrentReport(
        block: suspend (DailyReport) -> Result<T, StatsError>,
    ): Result<T, StatsError> {
        mutex.withLock {
            val today = clock.todayUtc()
            val result = dayReportPort.loadDay()
                .flatMap { storedReport ->
                    when {
                        storedReport == null -> Result.Success(DailyReport(date = today, usageList = emptyList()))
                        storedReport.date == today -> Result.Success(storedReport)
                        else -> archive(finishedReport = storedReport, today = today)
                    }
                }
                .flatMap { currentReport -> block(currentReport) }
            return result
        }
    }


    private suspend fun archive(
        finishedReport: DailyReport,
        today: LocalDate,
    ): Result<DailyReport, StatsError> {
        val cutoffDate = today.minus(MONTH_LENGTH_DAYS, DateTimeUnit.DAY)
        val freshReport = DailyReport(date = today, usageList = emptyList())

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
