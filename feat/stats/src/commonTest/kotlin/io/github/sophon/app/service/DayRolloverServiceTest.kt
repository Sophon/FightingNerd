package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.app.outPort.DayReportPort
import io.github.sophon.app.outPort.MonthReportPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import io.github.sophon.model.Usage
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Instant

class DayRolloverServiceTest {
    @Test
    fun `missing day report starts an empty one for today`() = runTest {
        // given
        val expected = Result.Success(DailyReport(date = today, usageList = emptyList()))
        val service = dayRolloverService(dayReportPort = FakeDayReportPort(storedReport = null))

        // when
        val result = service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `today's report is handed over as is`() = runTest {
        // given
        val expected = Result.Success(todayReport)
        val service = dayRolloverService(dayReportPort = FakeDayReportPort(storedReport = todayReport))

        // when
        val result = service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `today's report isn't archived`() = runTest {
        // given
        val monthReportPort = FakeMonthReportPort()
        val service = dayRolloverService(
            dayReportPort = FakeDayReportPort(storedReport = todayReport),
            monthReportPort = monthReportPort,
        )

        // when
        service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(monthReportPort.savedMonthList).isEmpty()
    }

    @Test
    fun `stale report is archived into the month`() = runTest {
        // given
        val expected = listOf(twoDaysAgoReport, yesterdayReport)
        val monthReportPort = FakeMonthReportPort(storedMonth = listOf(twoDaysAgoReport))
        val service = dayRolloverService(
            dayReportPort = FakeDayReportPort(storedReport = yesterdayReport),
            monthReportPort = monthReportPort,
        )

        // when
        service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(monthReportPort.savedMonthList).containsExactly(expected)
    }

    @Test
    fun `stale report is replaced by an empty one for today`() = runTest {
        // given
        val expected = DailyReport(date = today, usageList = emptyList())
        val dayReportPort = FakeDayReportPort(storedReport = yesterdayReport)
        val service = dayRolloverService(dayReportPort = dayReportPort)

        // when
        val result = service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(result).isEqualTo(Result.Success(expected))
        assertThat(dayReportPort.storedReport).isEqualTo(expected)
    }

    @Test
    fun `archived report replaces a month entry of the same date`() = runTest {
        // given
        val outdatedYesterdayReport = DailyReport(date = yesterday, usageList = listOf(fdUsage.copy(count = 1)))
        val expected = listOf(twoDaysAgoReport, yesterdayReport)
        val monthReportPort = FakeMonthReportPort(storedMonth = listOf(twoDaysAgoReport, outdatedYesterdayReport))
        val service = dayRolloverService(
            dayReportPort = FakeDayReportPort(storedReport = yesterdayReport),
            monthReportPort = monthReportPort,
        )

        // when
        service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(monthReportPort.savedMonthList).containsExactly(expected)
    }

    @Test
    fun `archived month drops reports older than the month length`() = runTest {
        // given
        val cutoffReport = DailyReport(date = LocalDate(2026, 9, 6), usageList = listOf(fdUsage))
        val tooOldReport = DailyReport(date = LocalDate(2026, 9, 5), usageList = listOf(fdUsage))
        val expected = listOf(cutoffReport, yesterdayReport)
        val monthReportPort = FakeMonthReportPort(storedMonth = listOf(tooOldReport, cutoffReport))
        val service = dayRolloverService(
            dayReportPort = FakeDayReportPort(storedReport = yesterdayReport),
            monthReportPort = monthReportPort,
        )

        // when
        service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(monthReportPort.savedMonthList).containsExactly(expected)
    }

    @Test
    fun `archived month is sorted by date`() = runTest {
        // given
        val threeDaysAgoReport = DailyReport(date = LocalDate(2026, 10, 3), usageList = listOf(helpUsage))
        val expected = listOf(threeDaysAgoReport, twoDaysAgoReport, yesterdayReport)
        val monthReportPort = FakeMonthReportPort(storedMonth = listOf(twoDaysAgoReport, threeDaysAgoReport))
        val service = dayRolloverService(
            dayReportPort = FakeDayReportPort(storedReport = yesterdayReport),
            monthReportPort = monthReportPort,
        )

        // when
        service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(monthReportPort.savedMonthList).containsExactly(expected)
    }

    @Test
    fun `block's result is returned`() = runTest {
        // given
        val expected = Result.Success(2)
        val service = dayRolloverService(dayReportPort = FakeDayReportPort(storedReport = todayReport))

        // when
        val result = service.withCurrentReport { currentReport -> Result.Success(currentReport.usageList.size) }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `block's error is returned`() = runTest {
        // given
        val error = StatsError.Unknown("block failed")
        val service = dayRolloverService(dayReportPort = FakeDayReportPort(storedReport = todayReport))

        // when
        val result = service.withCurrentReport { Result.Error(error) }

        // then
        assertThat(result).isEqualTo(Result.Error(error))
    }

    @Test
    fun `failed day load is returned and the block isn't run`() = runTest {
        // given
        val error = StatsError.FileError("day.json", "read failed")
        var blockReport: DailyReport? = null
        val service = dayRolloverService(dayReportPort = FakeDayReportPort(loadError = error))

        // when
        val result = service.withCurrentReport { currentReport ->
            blockReport = currentReport
            Result.Success(currentReport)
        }

        // then
        assertThat(result).isEqualTo(Result.Error(error))
        assertThat(blockReport).isNull()
    }

    @Test
    fun `failed month load keeps the stale day report`() = runTest {
        // given
        val error = StatsError.SerializationError("month.json", "malformed")
        val dayReportPort = FakeDayReportPort(storedReport = yesterdayReport)
        val service = dayRolloverService(
            dayReportPort = dayReportPort,
            monthReportPort = FakeMonthReportPort(loadError = error),
        )

        // when
        val result = service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(result).isEqualTo(Result.Error(error))
        assertThat(dayReportPort.storedReport).isEqualTo(yesterdayReport)
    }

    @Test
    fun `failed month save keeps the stale day report`() = runTest {
        // given
        val error = StatsError.FileError("month.json", "write failed")
        val dayReportPort = FakeDayReportPort(storedReport = yesterdayReport)
        val service = dayRolloverService(
            dayReportPort = dayReportPort,
            monthReportPort = FakeMonthReportPort(saveError = error),
        )

        // when
        val result = service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(result).isEqualTo(Result.Error(error))
        assertThat(dayReportPort.storedReport).isEqualTo(yesterdayReport)
    }

    @Test
    fun `failed fresh day save is returned`() = runTest {
        // given
        val error = StatsError.FileError("day.json", "write failed")
        val service = dayRolloverService(
            dayReportPort = FakeDayReportPort(storedReport = yesterdayReport, saveError = error),
        )

        // when
        val result = service.withCurrentReport { currentReport -> Result.Success(currentReport) }

        // then
        assertThat(result).isEqualTo(Result.Error(error))
    }

    @Test
    fun `concurrent calls don't lose each other's updates`() = runTest {
        // given
        val expected = listOf(fdUsage, helpUsage)
        val dayReportPort = FakeDayReportPort(storedReport = DailyReport(date = today, usageList = emptyList()))
        val service = dayRolloverService(dayReportPort = dayReportPort)

        // when
        listOf(fdUsage, helpUsage)
            .map { usage ->
                launch {
                    service.withCurrentReport { currentReport ->
                        dayReportPort.saveDay(currentReport.copy(usageList = (currentReport.usageList + usage)))
                    }
                }
            }
            .forEach { job -> job.join() }

        // then
        assertThat(dayReportPort.storedReport?.usageList).isEqualTo(expected)
    }


    private class FakeDayReportPort(
        var storedReport: DailyReport? = null,
        private val loadError: StatsError? = null,
        private val saveError: StatsError? = null,
    ): DayReportPort {
        override suspend fun loadDay(): Result<DailyReport?, StatsError> {
            if (loadError != null) return Result.Error(loadError)

            val loadedReport = storedReport
            // suspends between read and return like real IO would, so concurrent callers can interleave
            yield()
            return Result.Success(loadedReport)
        }

        override suspend fun saveDay(dailyReport: DailyReport): EmptyResult<StatsError> {
            if (saveError != null) return Result.Error(saveError)

            storedReport = dailyReport
            return Result.Success(Unit)
        }
    }

    private class FakeMonthReportPort(
        private val storedMonth: List<DailyReport> = emptyList(),
        private val loadError: StatsError? = null,
        private val saveError: StatsError? = null,
    ): MonthReportPort {
        val savedMonthList = mutableListOf<List<DailyReport>>()

        override suspend fun loadMonth(): Result<List<DailyReport>, StatsError> {
            if (loadError != null) return Result.Error(loadError)

            return Result.Success(storedMonth)
        }

        override suspend fun saveMonth(dailyReportList: List<DailyReport>): EmptyResult<StatsError> {
            if (saveError != null) return Result.Error(saveError)

            savedMonthList += dailyReportList
            return Result.Success(Unit)
        }
    }

    private class FixedClock(private val instant: Instant): Clock {
        override fun now(): Instant = instant
    }

    private fun dayRolloverService(
        dayReportPort: FakeDayReportPort = FakeDayReportPort(),
        monthReportPort: FakeMonthReportPort = FakeMonthReportPort(),
    ): DayRolloverServiceImpl {
        val service = DayRolloverServiceImpl(
            dayReportPort = dayReportPort,
            monthReportPort = monthReportPort,
            clock = FixedClock(now),
        )
        return service
    }
}


private val now = Instant.parse("2026-10-06T18:00:00Z")
private val today = LocalDate(2026, 10, 6)
private val yesterday = LocalDate(2026, 10, 5)
private val fdUsage = Usage(game = "Tekken 8", command = "Fd", count = 3)
private val helpUsage = Usage(game = null, command = "Help", count = 1)
private val todayReport = DailyReport(date = today, usageList = listOf(fdUsage, helpUsage))
private val yesterdayReport = DailyReport(date = yesterday, usageList = listOf(fdUsage))
private val twoDaysAgoReport = DailyReport(date = LocalDate(2026, 10, 4), usageList = listOf(helpUsage))
