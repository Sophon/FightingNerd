package io.github.sophon.discord.app.model.response

data class PlainTextResponse(
    val text: String,
    val buttonSet: BotResponse.ButtonSet? = null,
): BotResponse
