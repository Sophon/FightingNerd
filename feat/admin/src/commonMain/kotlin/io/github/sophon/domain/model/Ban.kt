package io.github.sophon.domain.model

import kotlinx.datetime.Instant

data class Ban(
    val offenderId: String,
    val bannedAt: Instant,
    val expiresAt: Instant,
    val authorId: String,
    val preventBotUsage: Boolean
)
