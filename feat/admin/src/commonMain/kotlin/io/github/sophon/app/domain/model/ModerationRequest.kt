package io.github.sophon.app.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class ModerationRequest(
    val authorId: String,
    val offenderId: String,
    val preventBotUsage: Boolean = false,
    val duration: Duration = 30.days,
)
