package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.DiscordConfig

internal interface LoadConfigPort {
    fun load(): Result<DiscordConfig, BotError>
}
