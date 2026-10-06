package io.github.sophon.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class BanRequest(
    val issuerId: String,
    val offenderId: String,
    val preventBotUsage: Boolean = false,
    val duration: Duration = 30.days,
)
