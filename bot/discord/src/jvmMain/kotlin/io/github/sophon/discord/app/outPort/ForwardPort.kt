package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.model.BotError

internal interface ForwardPort {
    suspend fun forward(
        sourceChannelId: String,
        sourceMessageId: String,
        targetChannelId: String,
    ): EmptyResult<BotError>
}
