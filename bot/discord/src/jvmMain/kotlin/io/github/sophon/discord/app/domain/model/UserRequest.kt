package io.github.sophon.discord.app.domain.model

import io.github.sophon.discord.feat.core.domain.model.Command

data class UserRequest(
    val command: Command?,
    val query: String,
    val source: Source,
) {
    data class Source(
        val username: String,
        val id: String,
        val channelId: String,
        val serverName: String = "",
    )
}
