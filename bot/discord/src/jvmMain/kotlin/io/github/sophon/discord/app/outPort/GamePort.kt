package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.GameList
import io.github.sophon.wiki.model.wiki.Game

internal interface GamePort {
    suspend fun getGameList(): GameList

    suspend fun findGame(gameId: String): Result<Game, BotError>
}
