package io.github.sophon.discord.featureRegistry.wikiWavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.wikiwavu.WavuWikiClient

class GetHomingMovesUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<Move>, BotError> {
        return when (val result = wiki.getHomingMoves(charName)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                Result.Error(result.error.toDomainError())
            }
        }
    }
}