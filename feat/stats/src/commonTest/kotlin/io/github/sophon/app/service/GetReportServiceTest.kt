package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import io.github.sophon.app.outPort.MonthReportPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import io.github.sophon.model.Usage
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class GetReportServiceTest {
    @Test
    fun `returns the month report`() = runTest {
        // given
        val expected = Result.Success(monthReport)
        val service = getReportService()

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `month is loaded after the rollover`() = runTest {
        // given
        val monthReportPort = FakeMonthReportPort()
        val service = getReportService(
            dayRolloverService = FakeDayRolloverService(onRollover = { monthReportPort.isRolledOver = true }),
            monthReportPort = monthReportPort,
        )

        // when
        service.invoke()

        // then
        assertThat(monthReportPort.isLoadedBeforeRollover).isFalse()
    }

    @Test
    fun `failed rollover is returned`() = runTest {
        // given
        val error = StatsError.FileError("day.json", "read failed")
        val service = getReportService(dayRolloverService = FakeDayRolloverService(error = error))

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Error(error))
    }

    @Test
    fun `failed month load is returned`() = runTest {
        // given
        val error = StatsError.SerializationError("month.json", "malformed")
        val service = getReportService(monthReportPort = FakeMonthReportPort(loadError = error))

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Error(error))
    }


    private class FakeDayRolloverService(
        private val error: StatsError? = null,
        private val onRollover: () -> Unit = {},
    ): DayRolloverService {
        override suspend fun <T> withCurrentReport(
            block: suspend (DailyReport) -> Result<T, StatsError>,
        ): Result<T, StatsError> {
            if (error != null) return Result.Error(error)

            onRollover()
            return block(DailyReport(date = LocalDate(2026, 10, 6), usageList = emptyList()))
        }
    }

    private class FakeMonthReportPort(
        private val loadError: StatsError? = null,
    ): MonthReportPort {
        var isRolledOver = false
        var isLoadedBeforeRollover = false

        override suspend fun loadMonth(): Result<List<DailyReport>, StatsError> {
            if (isRolledOver.not()) isLoadedBeforeRollover = true
            if (loadError != null) return Result.Error(loadError)

            return Result.Success(monthReport)
        }

        override suspend fun saveMonth(dailyReportList: List<DailyReport>): EmptyResult<StatsError> = Result.Success(Unit)
    }

    private fun getReportService(
        dayRolloverService: FakeDayRolloverService = FakeDayRolloverService(),
        monthReportPort: FakeMonthReportPort = FakeMonthReportPort(),
    ): GetReportService {
        val service = GetReportService(
            dayRolloverService = dayRolloverService,
            monthReportPort = monthReportPort,
        )
        return service
    }
}


private val monthReport = listOf(
    DailyReport(date = LocalDate(2026, 10, 4), usageList = listOf(Usage(game = null, command = "Help", count = 1))),
    DailyReport(date = LocalDate(2026, 10, 5), usageList = listOf(Usage(game = "Tekken 8", command = "Fd", count = 3))),
)
