package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.discord.URL_IMG_FIGHTING_NERD
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.wiki.model.wiki.Game

internal fun dailyReportEmbed(usageReport: UsageReport): EmbedBuilder.() -> Unit = {
    val totalCount = usageReport.usageList.sumOf { usage -> usage.count }
    title = "📊 Daily Report - ${usageReport.date} - ${totalCount}x"

    usageReport.usageList
        .groupBy { usage -> usage.game }
        .entries
        .sortedByDescending { (_, usageList) -> usageList.sumOf { usage -> usage.count } }
        .forEach { (game, usageList) ->
            val gameCategory = game?.let { Game.fromId(it)?.displayName ?: it } ?: NO_GAME_FIELD_NAME
            val stats = usageList
                .sortedByDescending { usage -> usage.count }
                .joinToString("\n") { usage ->
                    "`${usage.command.uppercase()}` - ${usage.count} hits"
                }
            mandatoryField(
                name = gameCategory,
                value = stats,
                inline = false,
            )
        }

    if (usageReport.usageList.isEmpty()) {
        description = "No commands recorded."
    }

    footer {
        text = "FightingNerd Stats"
        icon = URL_IMG_FIGHTING_NERD
    }
}


private const val NO_GAME_FIELD_NAME = "Other"
