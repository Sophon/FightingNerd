package io.github.sophon.discord.adapter.outbound.kord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.request.RestRequestException
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.adapter.inbound.kord.ui.dailyReportEmbed
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.outPort.PostReportPort

@ExcludeFromCoverage("UI")
internal class KordReportAdapter(
    private val kord: Kord,
): PostReportPort {
    /**
     * Tries every channel, a broken one doesn't stop the rest - the last error is returned.
     */
    override suspend fun post(channelIdList: List<String>, usageReport: UsageReport): EmptyResult<BotError> {
        val resultList = channelIdList.map { channelId -> post(channelId, usageReport) }
        val result = resultList.lastOrNull { it is Result.Error } ?: Result.Success(Unit)
        return result
    }


    private suspend fun post(channelId: String, usageReport: UsageReport): EmptyResult<BotError> {
        Napier.i(tag = TAG) { "Daily report to channel: $channelId" }

        val result = try {
            val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))
            if (channel == null) {
                Result.Error(BotError.Kord("Stats channel not found: $channelId"))
            } else {
                channel.createEmbed(dailyReportEmbed(usageReport))
                Result.Success(Unit)
            }
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
        return result
    }


    private companion object {
        const val TAG = "KordReportAdapter"
    }
}
