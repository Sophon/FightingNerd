package io.github.sophon.discord.app.port.outbound

internal interface RefreshWikiPort {
    suspend fun refresh()
}
