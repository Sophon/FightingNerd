package io.github.sophon.discord.adapter.outbound.stats

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.Command
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.outPort.StatsPort
import io.github.sophon.inPort.ConfigureStatsUseCase
import io.github.sophon.inPort.GetReportUseCase
import io.github.sophon.inPort.RecordUsageUseCase
import io.github.sophon.wiki.model.wiki.Game

internal class StatsAdapter(
    private val configureStatsUseCase: ConfigureStatsUseCase,
    private val recordUsageUseCase: RecordUsageUseCase,
    private val getReportUseCase: GetReportUseCase,
): StatsPort {
    override suspend fun configure(): EmptyResult<BotError> {
        val result = configureStatsUseCase()
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun register(command: Command, game: Game?): EmptyResult<BotError> {
        val result = recordUsageUseCase(command.toStatsCommand(game))
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun registerFailure(): EmptyResult<BotError> {
        val result = recordUsageUseCase(failedStatsCommand())
            .mapError { it.toDomainError() }
        return result
    }

    /**
     * The newest archived day - right after the UTC midnight rollover, that's the day that just ended.
     */
    override suspend fun getLatestReport(): Result<UsageReport?, BotError> {
        val result = getReportUseCase()
            .map { dailyReportList -> dailyReportList.lastOrNull()?.toDomain() }
            .mapError { it.toDomainError() }
        return result
    }
}
