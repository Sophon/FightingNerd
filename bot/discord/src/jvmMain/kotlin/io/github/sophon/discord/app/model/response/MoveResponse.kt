package io.github.sophon.discord.app.model.response

import io.github.sophon.wiki.model.wiki.Game

data class MoveResponse(
    override val game: Game,
    val input: String,
    val url: String?,
    val characterName: String,
    val characterUrl: String,
    val moveName: String?,
    val characterImageUrl: String?,
    val primaryFields: List<BotResponse.Field>,
    val dataSource: BotResponse.DataSource,
    val secondaryFields: List<BotResponse.Field> = emptyList(),
    val aliasList: List<String> = emptyList(),
    val noteList: List<String> = emptyList(),
    val videoUrl: String? = null,
    val hitboxImageList: List<String> = emptyList(),
    val imageList: List<String> = emptyList(),
    val stance: String? = null,

    val forceExpand: Boolean = false,
    val buttonSet: BotResponse.ButtonSet? = null,
): BotResponse
