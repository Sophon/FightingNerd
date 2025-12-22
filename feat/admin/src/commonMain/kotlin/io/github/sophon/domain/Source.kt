package io.github.sophon.domain

data class Source(
    val username: String,
    val id: String,
    val channelId: String,
    val serverName: String = "",
)
