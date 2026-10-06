package io.github.sophon.discord.app.outPort

import io.github.sophon.discord.app.model.GameList

internal interface GamePort {
    suspend fun getGameList(): GameList
}
