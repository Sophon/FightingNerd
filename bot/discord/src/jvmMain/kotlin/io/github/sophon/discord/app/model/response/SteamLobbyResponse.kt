package io.github.sophon.discord.app.model.response

data class SteamLobbyResponse(
    val hostName: String,
    val lobbyName: String?,
    val password: String?,
    val buttonSet: BotResponse.ButtonSet,
): BotResponse
