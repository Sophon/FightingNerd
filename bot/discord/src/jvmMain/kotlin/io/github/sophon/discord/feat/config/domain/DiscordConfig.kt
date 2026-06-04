package io.github.sophon.discord.feat.config.domain

import kotlinx.serialization.Serializable

@Serializable
internal data class DiscordConfig(
    val discordBotApiKey: String
)
