package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.DiscordConfig

internal interface ConfigureWikiPort {
    suspend fun configure(discordConfig: DiscordConfig): EmptyResult<BotError>
}
