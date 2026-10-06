package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import io.github.sophon.model.Usage
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class GetCurrentReportServiceTest {
    @Test
    fun `returns the current report`() = runTest {
        // given
        val expected = Result.Success(currentReport)
        val service = GetCurrentReportService(dayRolloverService = FakeDayRolloverService())

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed rollover is returned`() = runTest {
        // given
        val error = StatsError.FileError("day.json", "read failed")
        val service = GetCurrentReportService(dayRolloverService = FakeDayRolloverService(error = error))

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Error(error))
    }


    private class FakeDayRolloverService(
        private val error: StatsError? = null,
    ): DayRolloverService {
        override suspend fun <T> withCurrentReport(
            block: suspend (DailyReport) -> Result<T, StatsError>,
        ): Result<T, StatsError> {
            if (error != null) return Result.Error(error)

            return block(currentReport)
        }
    }
}


private val currentReport = DailyReport(
    date = LocalDate(2026, 10, 6),
    usageList = listOf(
        Usage(game = "Tekken 8", command = "Fd", count = 3),
        Usage(game = null, command = "Help", count = 1),
    ),
)
