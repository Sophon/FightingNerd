package io.github.sophon.domain.model

import io.github.sophon.core.util.toFormattedString
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Ban(
    val offenderId: String,
    val bannedAt: Instant,
    val expiresAt: Instant,
    val authorId: String,
    val preventBotUsage: Boolean
) {
    override fun toString(): String {
        return "BANNED: ${bannedAt.toFormattedString()} → ${expiresAt.toFormattedString()}"
    }
}
