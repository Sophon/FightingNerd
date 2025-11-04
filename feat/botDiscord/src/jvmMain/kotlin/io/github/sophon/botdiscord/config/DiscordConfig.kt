package io.github.sophon.botdiscord.config

import kotlinx.serialization.Serializable

//TODO: ConfigRepo
@Serializable
data class DiscordConfig(
    val discordBotApiKey: String
)