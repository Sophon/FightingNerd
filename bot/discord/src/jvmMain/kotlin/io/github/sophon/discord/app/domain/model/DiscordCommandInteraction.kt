package io.github.sophon.discord.app.domain.model

data class DiscordCommandInteraction(
    val username: String,
    val userId: String,
    val channelId: String,
    val command: String,
    val argumentMap: Map<String, String>,
    val serverName: String? = null,
)
