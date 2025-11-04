package io.github.sophon.discord.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomain
import io.github.sophon.wikiwavu.WavuWikiClient
import io.github.sophon.wikiwavu.domain.model.Move

class GetHomingMovesUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<Move>, BotError> {
        return when (val result = wiki.getHomingMoves(charName)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                Result.Error(result.error.toDomain())
            }
        }
    }
}