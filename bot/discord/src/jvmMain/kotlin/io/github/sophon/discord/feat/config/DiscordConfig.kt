package io.github.sophon.discord.feat.config

import kotlinx.serialization.Serializable

//TODO: ConfigRepo
@Serializable
data class DiscordConfig(
    val discordBotApiKey: String
)