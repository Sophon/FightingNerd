package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.domain.model.DiscordJsonConfig
import io.github.sophon.discord.feat.core.domain.model.BotError

internal interface ConfigureWikiPort {
    suspend fun configure(discordJsonConfig: DiscordJsonConfig): EmptyResult<BotError>
}
