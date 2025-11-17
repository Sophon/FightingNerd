package io.github.sophon.discord.config

import kotlinx.serialization.Serializable

//TODO: ConfigRepo
@Serializable
data class DiscordConfig(
    val discordBotApiKey: String
)