package io.github.sophon.discord.app.model.response

import io.github.sophon.discord.app.model.UserRequest

data class ReplyResponse(
    val recipient: UserRequest.Source,
    val message: String,
): BotResponse
