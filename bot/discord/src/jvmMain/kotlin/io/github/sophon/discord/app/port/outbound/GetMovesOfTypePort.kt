package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.MoveType

internal interface GetMovesOfTypePort {
    suspend fun getMovesOfType(characterQuery: String, moveType: MoveType): Result<BotResponse.ListResponse, BotError>

    suspend fun getStances(characterQuery: String): Result<BotResponse.ListResponse, BotError>

    suspend fun getStanceMoves(characterQuery: String, stanceQuery: String): Result<BotResponse.ListResponse, BotError>

    suspend fun getMovesStartingWith(characterQuery: String, prefix: String): Result<BotResponse.ListResponse, BotError>
}
