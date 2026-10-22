package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.DayReportPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.Command
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import io.github.sophon.model.Usage
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class RecordUsageServiceTest {
    @Test
    fun `first use of a command is counted once`() = runTest {
        // given
        val expected = DailyReport(date = today, usageList = listOf(fdUsage, Usage(game = null, command = "Help", count = 1)))
        val dayReportPort = FakeDayReportPort()
        val service = recordUsageService(
            currentReport = DailyReport(date = today, usageList = listOf(fdUsage)),
            dayReportPort = dayReportPort,
        )

        // when
        service.invoke(Command(game = null, name = "Help"))

        // then
        assertThat(dayReportPort.savedReportList).containsExactly(expected)
    }

    @Test
    fun `repeated command is incremented`() = runTest {
        // given
        val expected = DailyReport(date = today, usageList = listOf(fdUsage.copy(count = 4), glossaryUsage))
        val dayReportPort = FakeDayReportPort()
        val service = recordUsageService(
            currentReport = DailyReport(date = today, usageList = listOf(fdUsage, glossaryUsage)),
            dayReportPort = dayReportPort,
        )

        // when
        service.invoke(Command(game = "Tekken 8", name = "Fd"))

        // then
        assertThat(dayReportPort.savedReportList).containsExactly(expected)
    }

    @Test
    fun `same command in another game is counted separately`() = runTest {
        // given
        val expected = DailyReport(
            date = today,
            usageList = listOf(fdUsage, Usage(game = "Street Fighter 6", command = "Fd", count = 1)),
        )
        val dayReportPort = FakeDayReportPort()
        val service = recordUsageService(
            currentReport = DailyReport(date = today, usageList = listOf(fdUsage)),
            dayReportPort = dayReportPort,
        )

        // when
        service.invoke(Command(game = "Street Fighter 6", name = "Fd"))

        // then
        assertThat(dayReportPort.savedReportList).containsExactly(expected)
    }

    @Test
    fun `gameless command is counted separately from the same command with a game`() = runTest {
        // given
        val expected = DailyReport(
            date = today,
            usageList = listOf(fdUsage, Usage(game = null, command = "Fd", count = 1)),
        )
        val dayReportPort = FakeDayReportPort()
        val service = recordUsageService(
            currentReport = DailyReport(date = today, usageList = listOf(fdUsage)),
            dayReportPort = dayReportPort,
        )

        // when
        service.invoke(Command(game = null, name = "Fd"))

        // then
        assertThat(dayReportPort.savedReportList).containsExactly(expected)
    }

    @Test
    fun `recorded usage is a success`() = runTest {
        // given
        val service = recordUsageService()

        // when
        val result = service.invoke(Command(game = "Tekken 8", name = "Fd"))

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `failed rollover is returned and nothing is saved`() = runTest {
        // given
        val error = StatsError.FileError("day.json", "read failed")
        val dayReportPort = FakeDayReportPort()
        val service = recordUsageService(
            dayRolloverService = FakeDayRolloverService(error = error),
            dayReportPort = dayReportPort,
        )

        // when
        val result = service.invoke(Command(game = "Tekken 8", name = "Fd"))

        // then
        assertThat(result).isEqualTo(Result.Error(error))
        assertThat(dayReportPort.savedReportList).isEmpty()
    }

    @Test
    fun `failed save is returned`() = runTest {
        // given
        val error = StatsError.FileError("day.json", "write failed")
        val service = recordUsageService(dayReportPort = FakeDayReportPort(saveError = error))

        // when
        val result = service.invoke(Command(game = "Tekken 8", name = "Fd"))

        // then
        assertThat(result).isEqualTo(Result.Error(error))
    }


    private class FakeDayRolloverService(
        private val currentReport: DailyReport = DailyReport(date = today, usageList = emptyList()),
        private val error: StatsError? = null,
    ): DayRolloverService {
        override suspend fun <T> withCurrentReport(
            block: suspend (DailyReport) -> Result<T, StatsError>,
        ): Result<T, StatsError> {
            if (error != null) return Result.Error(error)

            return block(currentReport)
        }
    }

    private class FakeDayReportPort(
        private val saveError: StatsError? = null,
    ): DayReportPort {
        val savedReportList = mutableListOf<DailyReport>()

        override suspend fun loadDay(): Result<DailyReport?, StatsError> = Result.Success(savedReportList.lastOrNull())

        override suspend fun saveDay(dailyReport: DailyReport): EmptyResult<StatsError> {
            if (saveError != null) return Result.Error(saveError)

            savedReportList += dailyReport
            return Result.Success(Unit)
        }
    }

    private fun recordUsageService(
        currentReport: DailyReport = DailyReport(date = today, usageList = emptyList()),
        dayRolloverService: FakeDayRolloverService = FakeDayRolloverService(currentReport = currentReport),
        dayReportPort: FakeDayReportPort = FakeDayReportPort(),
    ): RecordUsageService {
        val service = RecordUsageService(
            dayRolloverService = dayRolloverService,
            dayReportPort = dayReportPort,
        )
        return service
    }
}


private val today = LocalDate(2026, 10, 6)
private val fdUsage = Usage(game = "Tekken 8", command = "Fd", count = 3)
private val glossaryUsage = Usage(game = null, command = "Glossary", count = 2)
