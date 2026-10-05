package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.DayRollover
import io.github.sophon.app.outPort.DayReportPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.onError
import io.github.sophon.inPort.RecordUsageUseCase
import io.github.sophon.model.Command
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError

internal class RecordUsageService(
    private val dayRollover: DayRollover,
    private val dayReportPort: DayReportPort,
) : RecordUsageUseCase {
    override suspend fun invoke(command: Command): EmptyResult<StatsError> {
        val result = dayRollover
            .withCurrentReport { currentReport ->
                val updatedReport = currentReport.increment(command)
                dayReportPort.saveDay(updatedReport)
            }
            .onError { error -> Napier.e(tag = TAG) { "$command: ${error.errors.joinToString()}" } }
        return result
    }


    private fun DailyReport.increment(command: Command): DailyReport {
        val featureCommandMap = commandMap[command.game].orEmpty()
        val count = ((featureCommandMap[command.name] ?: 0L) + 1)
        val updatedFeatureCommandMap = (featureCommandMap + (command.name to count))
        val updated = copy(commandMap = (commandMap + (command.game to updatedFeatureCommandMap)))
        return updated
    }


    private companion object {
        const val TAG = "RecordUsageService"
    }
}
