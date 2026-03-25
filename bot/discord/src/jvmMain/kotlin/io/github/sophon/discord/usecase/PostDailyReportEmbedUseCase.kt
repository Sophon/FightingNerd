package io.github.sophon.discord.usecase

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.TextChannel
import io.github.aakira.napier.Napier
import io.github.sophon.domain.model.DailyReport

internal class PostDailyReportEmbedUseCase(
    private val kord: Kord,
) {
    suspend fun invoke(
        statsChannelId: String,
        dailyReport: DailyReport,
    ) {
        Napier.i(tag = TAG) { "Daily report to channel: $statsChannelId" }

        val channel = kord.getChannelOf<TextChannel>(Snowflake(statsChannelId))
            ?: run {
                Napier.e(tag = TAG) { "Stats channel not found: $statsChannelId" }
                return
            }

        channel.createEmbed {
            val totalUses = dailyReport.commandMap.values.sumOf { command -> command.values.sum() }
            title = "📊 Daily Report — ${dailyReport.date} — ${totalUses}x"
            dailyReport.commandMap.forEach { (feature, commands) ->
                field {
                    name = feature
                    value = commands.entries.joinToString("\n") { (cmd, count) ->
                        "`$cmd` — $count hits"
                    }
                    inline = false
                }
            }
            if (dailyReport.commandMap.isEmpty()) {
                description = "No commands recorded today."
            }

            footer { text = "FightingNerd Stats" }
        }
    }

    private companion object {
        const val TAG = "PostDailyReportEmbedUseCase"
    }
}