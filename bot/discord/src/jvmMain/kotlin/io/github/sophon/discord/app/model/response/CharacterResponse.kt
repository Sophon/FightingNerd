package io.github.sophon.discord.app.model.response

import io.github.sophon.wiki.model.wiki.Game

data class CharacterResponse(
    val id: String,
    override val game: Game,
    val displayName: String,
    val url: String,
    val dataSource: BotResponse.DataSource,
    val aliasList: List<String> = emptyList(),
    val propertyList: List<BotResponse.Field> = emptyList(),
): BotResponse
