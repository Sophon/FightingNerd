package io.github.sophon.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

interface GetCurrentReportUseCase {
    suspend operator fun invoke(): Result<DailyReport, StatsError>
}
