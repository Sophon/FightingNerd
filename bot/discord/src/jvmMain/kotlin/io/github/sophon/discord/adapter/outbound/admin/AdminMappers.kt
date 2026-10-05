package io.github.sophon.discord.adapter.outbound.admin

import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest
import io.github.sophon.model.UnbanRequest
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.ModerationRequest

internal fun ModerationRequest.toBanRequest(): BanRequest {
    val banRequest = BanRequest(
        issuerId = authorId,
        offenderId = offenderId,
        preventBotUsage = preventBotUsage,
        duration = duration,
    )
    return banRequest
}

internal fun ModerationRequest.toUnbanRequest(): UnbanRequest {
    val unbanRequest = UnbanRequest(
        issuerId = authorId,
        offenderId = offenderId,
    )
    return unbanRequest
}

internal fun Ban.toDomain(): BotResponse.Ban {
    val ban = BotResponse.Ban(
        offenderId = offenderId,
        bannedAt = bannedAt,
        expiresAt = expiresAt,
        issuerId = issuerId,
        preventBotUsage = preventBotUsage,
    )
    return ban
}

internal fun AdminError.toDomainError(): BotError {
    val botError = when (this) {
        is AdminError.PermissionDenied -> BotError.PermissionDenied()
        is AdminError.Database -> BotError.AdminError(this.toString())
    }

    return botError
}
