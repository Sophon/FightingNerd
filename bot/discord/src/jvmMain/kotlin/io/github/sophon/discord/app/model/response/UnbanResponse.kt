package io.github.sophon.discord.app.model.response

import io.github.sophon.discord.app.model.UserRequest

data class UnbanResponse(
    val offender: UserRequest.Source,
): BotResponse
