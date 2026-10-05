package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.BotResponse
import io.github.sophon.discord.app.model.ModerationRequest

internal interface BanPort {
    suspend fun ban(moderationRequest: ModerationRequest): Result<BotResponse.Ban, BotError>
    suspend fun unban(moderationRequest: ModerationRequest): EmptyResult<BotError>
    suspend fun isBanned(userId: String): Result<Boolean, BotError>
}
