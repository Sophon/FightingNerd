package io.github.sophon.discord.app.model.response

data class GlossaryResponse(
    val dataSource: BotResponse.DataSource,
    val term: String,
    val definition: String,
    val jpTranslationList: List<String>,
    val termUrl: String,
    val videoUrl: String?,
    val imageUrl: String?,
): BotResponse
