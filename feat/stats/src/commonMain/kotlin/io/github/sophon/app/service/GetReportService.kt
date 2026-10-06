package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.MonthReportPort
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.inPort.GetReportUseCase
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

internal class GetReportService(
    private val dayRolloverService: DayRolloverService,
    private val monthReportPort: MonthReportPort,
) : GetReportUseCase {
    override suspend fun invoke(): Result<List<DailyReport>, StatsError> {
        val result = dayRolloverService
            .withCurrentReport { monthReportPort.loadMonth() }
            .onError { error -> Napier.e(tag = TAG) { error.errors.joinToString() } }
        return result
    }


    private companion object {
        const val TAG = "GetReportService"
    }
}
