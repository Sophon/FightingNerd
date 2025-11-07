package io.github.sophon.discord.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.WavuWikiClient

class GetHeatMovesUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<Move>, BotError> {
        return when (val result = wiki.getHeatMoves(charName)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                //TODO: mapper
                Result.Error(
                    when (result.error) {
                        WavuError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
                        WavuError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
                        WavuError.DOWNLOAD_ERROR -> BotError.DOWNLOAD_ERROR
                        WavuError.DATABASE_ERROR -> BotError.UNKNOWN
                    }
                )
            }
        }
    }
}