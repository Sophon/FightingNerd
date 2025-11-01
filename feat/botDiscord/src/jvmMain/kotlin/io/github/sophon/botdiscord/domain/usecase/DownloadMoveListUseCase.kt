package io.github.sophon.botdiscord.domain.usecase

import io.github.sophon.botdiscord.BotError
import io.github.sophon.botdiscord.domain.toDomain
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.wikiwavu.WavuWikiClient

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