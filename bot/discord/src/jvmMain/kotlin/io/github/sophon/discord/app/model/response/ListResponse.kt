package io.github.sophon.discord.app.model.response

import io.github.sophon.wiki.model.wiki.Game

data class ListResponse(
    override val game: Game,
    val title: String,
    val values: List<String>,
    val dataSource: BotResponse.DataSource,
    val buttonSet: BotResponse.ButtonSet? = null,
): BotResponse
