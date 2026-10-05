package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.BotResponse
import io.github.sophon.discord.app.model.CharacterId
import io.github.sophon.discord.app.model.FrameRange

internal interface GetMovesInRangePort {
    suspend fun getMovesInRange(
        characterId: CharacterId,
        frameRange: FrameRange,
    ): Result<List<BotResponse.MoveResponse>, BotError>
}
