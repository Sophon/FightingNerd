package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.model.CharacterId
import io.github.sophon.discord.app.model.MoveType

internal interface GetMovesOfTypePort {
    suspend fun getMovesOfType(
        characterId: CharacterId,
        moveType: MoveType,
    ): Result<List<MoveResponse>, BotError>
}
