package io.github.sophon.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

interface GetReportUseCase {
    suspend operator fun invoke(): Result<List<DailyReport>, StatsError>
}
