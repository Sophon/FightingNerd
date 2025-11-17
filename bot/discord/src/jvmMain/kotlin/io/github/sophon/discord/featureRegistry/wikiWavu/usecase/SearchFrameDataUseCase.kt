package io.github.sophon.discord.featureRegistry.wikiWavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.parseQuery
import io.github.sophon.wikiwavu.WavuWikiClient

internal class SearchFrameDataUseCase(
    private val wavuWikiClient: WavuWikiClient,
) {
    suspend fun invoke(query: String): Result<Move, BotError> {
        val parsedQuery = query.parseQuery()
        if (parsedQuery == null) return Result.Error(BotError.UnknownMove(query))

        return when (
            val result = wavuWikiClient.frameDataFor(charName = parsedQuery.charName, moveQuery = parsedQuery.move)
        ) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.error.toDomainError())
        }
    }
}