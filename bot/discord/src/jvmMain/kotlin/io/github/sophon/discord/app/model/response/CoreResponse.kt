package io.github.sophon.discord.app.model.response

data class CoreResponse(
    val type: Type,
    val dataSource: BotResponse.DataSource,
    val buttonSet: BotResponse.ButtonSet? = null,
): BotResponse {
    enum class Type {
        Tip,
        Help,
        Commands,
    }
}
