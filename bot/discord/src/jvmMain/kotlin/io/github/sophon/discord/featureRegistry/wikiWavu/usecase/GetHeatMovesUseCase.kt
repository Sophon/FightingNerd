package io.github.sophon.discord.featureRegistry.wikiWavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
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
                        WikiError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
                        WikiError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
                        WikiError.DOWNLOAD_ERROR -> BotError.DOWNLOAD_ERROR
                        WikiError.DATABASE_ERROR -> BotError.UNKNOWN
                    }
                )
            }
        }
    }
}