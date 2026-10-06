package io.github.sophon.discord.adapter.outbound.stats

import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import io.github.sophon.model.Usage
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.model.Command as StatsCommand

internal fun Command.toStatsCommand(game: Game?): StatsCommand {
    val statsCommand = StatsCommand(
        game = game?.id,
        name = name,
    )
    return statsCommand
}

internal fun failedStatsCommand(): StatsCommand {
    val statsCommand = StatsCommand(
        game = null,
        name = FAILED_COMMAND_NAME,
    )
    return statsCommand
}

internal fun DailyReport.toDomain(): UsageReport {
    val usageReport = UsageReport(
        date = date,
        usageList = usageList.map { usage -> usage.toDomain() },
    )
    return usageReport
}

internal fun StatsError.toDomainError(): BotError {
    val botError = when (this) {
        is StatsError.FileError -> BotError.FileError(*errors)
        is StatsError.SerializationError -> BotError.FileError(*errors)
        is StatsError.Unknown -> BotError.Unknown(errors.joinToString())
    }
    return botError
}


private fun Usage.toDomain(): UsageReport.Usage {
    val usage = UsageReport.Usage(
        game = game,
        command = command,
        count = count,
    )
    return usage
}


private const val FAILED_COMMAND_NAME = "failed"
