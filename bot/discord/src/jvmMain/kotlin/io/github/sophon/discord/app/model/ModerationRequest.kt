package io.github.sophon.discord.app.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class ModerationRequest(
    val authorId: String,
    val offender: UserRequest.Source,
    val preventBotUsage: Boolean = false,
    val duration: Duration = 30.days,
)
