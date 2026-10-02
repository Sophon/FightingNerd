package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.feat.core.domain.model.BotError

internal interface ConfigureWikiPort {
    suspend fun configure(discordConfig: DiscordConfig): EmptyResult<BotError>
}
