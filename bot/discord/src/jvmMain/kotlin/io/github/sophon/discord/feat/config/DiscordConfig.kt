package io.github.sophon.discord.feat.config

import kotlinx.serialization.Serializable

@Serializable
internal data class DiscordConfig(
    val discordBotApiKey: String
)