package io.github.sophon.discord.adapter.outbound.admin

import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest
import io.github.sophon.model.UnbanRequest
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.BotResponse
import io.github.sophon.discord.app.model.ModerationRequest
import io.github.sophon.discord.app.model.UserRequest

internal fun ModerationRequest.toBanRequest(): BanRequest {
    val banRequest = BanRequest(
        issuerId = authorId,
        offenderId = offender.id,
        preventBotUsage = preventBotUsage,
        duration = duration,
    )
    return banRequest
}

internal fun ModerationRequest.toUnbanRequest(): UnbanRequest {
    val unbanRequest = UnbanRequest(
        issuerId = authorId,
        offenderId = offender.id,
    )
    return unbanRequest
}

internal fun Ban.toDomain(offender: UserRequest.Source): BotResponse.Ban {
    val ban = BotResponse.Ban(
        offender = offender,
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
