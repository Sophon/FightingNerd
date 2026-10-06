package io.github.sophon.discord.app.model.response

import io.github.sophon.core.util.toFormattedString
import io.github.sophon.discord.app.model.UserRequest
import kotlin.time.Instant

data class BanResponse(
    val offender: UserRequest.Source,
    val bannedAt: Instant,
    val expiresAt: Instant,
    val issuerId: String,
    val preventBotUsage: Boolean,
): BotResponse {
    override fun toString(): String {
        return "BANNED: ${bannedAt.toFormattedString()} → ${expiresAt.toFormattedString()}"
    }
}
