package io.github.sophon.discord.adapter.outbound.admin

import io.github.sophon.discord.BOT_DATA_SOURCE
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.ModerationRequest
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest
import io.github.sophon.model.UnbanRequest

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

internal fun Ban.toDomain(offender: UserRequest.Source): BanResponse {
    val ban = BanResponse(
        offender = offender,
        bannedAt = bannedAt,
        expiresAt = expiresAt,
        issuerId = issuerId,
        preventBotUsage = preventBotUsage,
        dataSource = BOT_DATA_SOURCE,
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
