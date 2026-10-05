package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.DiscordConfig

internal interface ConfigureAdminPort {
    fun configure(discordConfig: DiscordConfig): EmptyResult<BotError>
}
