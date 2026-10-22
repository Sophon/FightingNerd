package io.github.sophon.discord.app.model

import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.wiki.model.wiki.Game

data class GameList(
    val gameList: List<Game>,
    val dataSource: BotResponse.DataSource,
)
