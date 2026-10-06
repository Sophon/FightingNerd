package io.github.sophon.discord.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.github.sophon.discord.app.outPort.PostReportPort
import io.github.sophon.discord.app.outPort.StatsPort
import io.github.sophon.discord.inPort.PostDailyReportUseCase

internal class PostDailyReportService(
    private val loadConfigPort: LoadConfigPort,
    private val statsPort: StatsPort,
    private val postReportPort: PostReportPort,
): PostDailyReportUseCase {
    override suspend fun invoke(): EmptyResult<BotError> {
        val result = statsPort.getLatestReport()
            .flatMap { usageReport ->
                if (usageReport == null) {
                    Napier.i(tag = TAG) { "No archived report yet, nothing to post" }
                    Result.Success(Unit)
                } else {
                    post(usageReport)
                }
            }
        return result
    }


    private suspend fun post(usageReport: UsageReport): EmptyResult<BotError> {
        val result = loadConfigPort.load()
            .flatMap { discordConfig ->
                postReportPort.post(
                    channelIdList = discordConfig.statsConfig.statsChannelIdList,
                    usageReport = usageReport,
                )
            }
        return result
    }


    private companion object {
        const val TAG = "PostDailyReportService"
    }
}
