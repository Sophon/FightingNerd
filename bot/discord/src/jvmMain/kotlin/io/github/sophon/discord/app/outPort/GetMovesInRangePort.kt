package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.FrameRange
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.response.MoveResponse

internal interface GetMovesInRangePort {
    suspend fun getMovesInRange(
        characterId: CharacterId,
        frameRange: FrameRange,
    ): Result<List<MoveResponse>, BotError>
}
