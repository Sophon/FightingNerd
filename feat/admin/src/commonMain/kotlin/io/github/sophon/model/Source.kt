package io.github.sophon.model

data class Source(
    val username: String,
    val id: String,
    val channelId: String,
    val serverName: String = "",
)