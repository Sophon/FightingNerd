package com.example.botdiscord.domain.usecase

import com.example.botdiscord.BotError
import com.example.botdiscord.domain.toDomain
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.wikiwavu.WavuWikiClient

class DownloadMoveListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): EmptyResult<BotError> {
        return when (val charListResult = wiki.downloadCharacterList()) {
            is Result.Success -> {
                for (character in charListResult.data) {
                    return when (val moveListResult = wiki.downloadMoveListFor(character.name)) {
                        is Result.Success -> {
                            when (val cacheResult = wiki.cacheMoveList(character, moveListResult.data)) {
                                is Result.Success -> continue
                                is Result.Error -> Result.Error(cacheResult.error.toDomain())
                            }
                        }

                        is Result.Error -> Result.Error(moveListResult.error.toDomain())
                    }
                }
                Result.Success(Unit)
            }
            is Result.Error -> Result.Error(charListResult.error.toDomain())
        }
    }
}