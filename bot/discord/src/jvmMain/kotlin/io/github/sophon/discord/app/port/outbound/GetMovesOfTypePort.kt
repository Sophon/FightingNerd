package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.CharacterId
import io.github.sophon.discord.app.domain.model.MoveType

internal interface GetMovesOfTypePort {
    suspend fun getMovesOfType(
        characterId: CharacterId,
        moveType: MoveType,
    ): Result<List<BotResponse.MoveResponse>, BotError>
}
