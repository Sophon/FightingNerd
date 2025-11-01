package io.github.sophon.botdiscord.domain.usecase

import io.github.sophon.botdiscord.BotError
import io.github.sophon.botdiscord.domain.toDomain
import io.github.sophon.core.domain.Result
import io.github.sophon.core.util.dropFirstAndJoin
import io.github.sophon.wikiwavu.WavuWikiClient
import io.github.sophon.wikiwavu.domain.model.Move

internal class SearchFrameDataUseCase(
    private val wavuWikiClient: WavuWikiClient,
) {
    suspend fun invoke(query: String): Result<Move, BotError> {
        val parsedQuery = parseQuery(query)
        if (parsedQuery == null) return Result.Error(BotError.INVALID_QUERY)

        return when (
            val result = wavuWikiClient.frameDataFor(charName = parsedQuery.charName, moveQuery = parsedQuery.move)
        ) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.error.toDomain())
        }
    }

    private fun parseQuery(query: String): ParsedQuery? {
        if (query.split(" ").size < 2) return null

        val charName = query.substringBefore(' ')
        val move = query.dropFirstAndJoin(' ')

        return ParsedQuery(charName, move)
    }

    private data class ParsedQuery(
        val charName: String,
        val move: String,
    )
}