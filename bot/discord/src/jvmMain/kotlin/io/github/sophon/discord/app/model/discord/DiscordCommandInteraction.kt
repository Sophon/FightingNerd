package io.github.sophon.discord.app.model.discord

data class DiscordCommandInteraction(
    val username: String,
    val userId: String,
    val channelId: String,
    val command: String,
    val argumentMap: Map<String, String>,
    val serverName: String? = null,
)
