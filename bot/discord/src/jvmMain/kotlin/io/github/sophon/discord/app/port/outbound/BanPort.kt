package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.ModerationRequest

internal interface BanPort {
    suspend fun ban(moderationRequest: ModerationRequest): Result<BotResponse.Ban, BotError>
    suspend fun unban(moderationRequest: ModerationRequest): EmptyResult<BotError>
    suspend fun isBanned(userId: String): Result<Boolean, BotError>
}
