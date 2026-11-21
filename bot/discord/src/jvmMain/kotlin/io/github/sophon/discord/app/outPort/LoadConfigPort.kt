package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.DiscordConfig

internal interface LoadConfigPort {
    suspend fun load(): Result<DiscordConfig, BotError>

    suspend fun download(): Result<DiscordConfig, BotError>
}
