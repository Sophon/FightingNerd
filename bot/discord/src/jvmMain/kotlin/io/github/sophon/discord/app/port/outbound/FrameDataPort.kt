package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.CharacterId
import io.github.sophon.discord.app.domain.model.MoveId
import io.github.sophon.discord.app.domain.model.BotError

internal interface FrameDataPort {
    suspend fun getMoves(characterId: CharacterId): Result<List<BotResponse.MoveResponse>, BotError>

    suspend fun getFrameData(moveId: MoveId): Result<BotResponse.MoveResponse, BotError>
}
