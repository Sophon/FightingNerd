package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.DayReportPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.onError
import io.github.sophon.inPort.RecordUsageUseCase
import io.github.sophon.model.Command
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import io.github.sophon.model.Usage

internal class RecordUsageService(
    private val dayRolloverService: DayRolloverService,
    private val dayReportPort: DayReportPort,
) : RecordUsageUseCase {
    override suspend fun invoke(command: Command): EmptyResult<StatsError> {
        val result = dayRolloverService
            .withCurrentReport { currentReport ->
                val updatedReport = currentReport.increment(command)
                dayReportPort.saveDay(updatedReport)
            }
            .onError { error -> Napier.e(tag = TAG) { "$command: ${error.errors.joinToString()}" } }
        return result
    }


    private fun DailyReport.increment(command: Command): DailyReport {
        val updatedUsageList = if (usageList.any { usage -> usage.isOf(command) }) {
            usageList.map { usage -> if (usage.isOf(command)) usage.copy(count = (usage.count + 1)) else usage }
        } else {
            (usageList + Usage(game = command.game, command = command.name, count = 1))
        }
        val updated = copy(usageList = updatedUsageList)
        return updated
    }

    private fun Usage.isOf(command: Command): Boolean {
        val isOf = ((game == command.game) && (this.command == command.name))
        return isOf
    }


    private companion object {
        const val TAG = "RecordUsageService"
    }
}
