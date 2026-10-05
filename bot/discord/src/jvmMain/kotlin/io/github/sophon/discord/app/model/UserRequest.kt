package io.github.sophon.discord.app.model

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
    ) {
        companion object {
            // `username-id-channelId
            fun parse(handle: String): Source? {
                val partList = handle.trim().split("-")
                if ((partList.size != 3) || partList.any { it.isBlank() }) return null

                val (username, id, channelId) = partList
                val source = Source(username = username, id = id, channelId = channelId)
                return source
            }
        }
    }
}
