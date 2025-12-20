package io.github.sophon.domain.model

import kotlinx.datetime.Instant

data class Ban(
    val userId: String,
    val bannedAt: Instant,
    val expiresAt: Instant,
    val preventBotUsage: Boolean
)