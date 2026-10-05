package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.DayRollover
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.inPort.GetCurrentReportUseCase
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

internal class GetCurrentReportService(
    private val dayRollover: DayRollover,
) : GetCurrentReportUseCase {
    override suspend fun invoke(): Result<DailyReport, StatsError> {
        val result = dayRollover
            .withCurrentReport { currentReport -> Result.Success(currentReport) }
            .onError { error -> Napier.e(tag = TAG) { error.errors.joinToString() } }
        return result
    }


    private companion object {
        const val TAG = "GetCurrentReportService"
    }
}
