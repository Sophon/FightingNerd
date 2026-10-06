package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.model.CharacterId
import io.github.sophon.discord.app.model.MoveId

internal interface FrameDataPort {
    suspend fun getMoves(characterId: CharacterId): Result<List<MoveResponse>, BotError>

    suspend fun getFrameData(moveId: MoveId): Result<MoveResponse, BotError>
}
