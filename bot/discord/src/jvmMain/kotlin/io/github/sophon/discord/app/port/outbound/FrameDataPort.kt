package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.feat.core.domain.model.BotError

internal interface FrameDataPort {
    suspend fun getFrameData(query: String): Result<BotResponse, BotError>
}
