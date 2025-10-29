package com.example.botdiscord.domain.usecase

import com.example.botdiscord.BotError
import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.WavuWikiClient
import com.example.wikiwavu.domain.model.Move

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
                        WavuError.CHARACTER_LIST_NOT_FOUND,
                        WavuError.CHARACTER_SERIALIZATION_ERROR,
                            -> BotError.CHARACTER_LIST_FILE_ERROR
                        WavuError.DATABASE_ERROR -> BotError.UNKNOWN
                    }
                )
            }
        }
    }
}