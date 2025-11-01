package com.example.botdiscord.domain.usecase

import com.example.botdiscord.BotError
import com.example.botdiscord.domain.toDomain
import io.github.sophon.core.domain.Result
import com.example.wikiwavu.WavuWikiClient
import com.example.wikiwavu.domain.model.Move

class GetPowerCrushMovesUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<Move>, BotError> {
        return when (val result = wiki.getPowerCrushMoves(charName)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.error.toDomain())
        }
    }
}