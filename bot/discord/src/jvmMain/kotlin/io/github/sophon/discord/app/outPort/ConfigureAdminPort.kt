package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.DiscordConfig

internal interface ConfigureAdminPort {
    fun configure(discordConfig: DiscordConfig): EmptyResult<BotError>
}
