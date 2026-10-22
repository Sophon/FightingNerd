package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.wiki.model.wiki.Game

internal interface StatsPort {
    suspend fun configure(): EmptyResult<BotError>
    suspend fun register(command: Command, game: Game?): EmptyResult<BotError>
    suspend fun registerFailure(): EmptyResult<BotError>
    suspend fun getLatestReport(): Result<UsageReport?, BotError>
}
