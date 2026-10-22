package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UsageReport

internal interface PostReportPort {
    suspend fun post(channelIdList: List<String>, usageReport: UsageReport): EmptyResult<BotError>
}
