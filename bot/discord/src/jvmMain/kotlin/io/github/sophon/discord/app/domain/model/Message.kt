package io.github.sophon.discord.app.domain.model

data class Message(
    val serverName: String,
    val channelId: String,
    val author: Author,
    val isFromBot: Boolean,
    val content: String,
) {
    data class Author(
        val id: String,
        val username: String,
    )
}
