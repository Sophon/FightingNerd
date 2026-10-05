package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.CharacterId
import io.github.sophon.discord.app.domain.model.FrameRange

internal interface GetMovesInRangePort {
    suspend fun getMovesInRange(
        characterId: CharacterId,
        frameRange: FrameRange,
    ): Result<List<BotResponse.MoveResponse>, BotError>
}
