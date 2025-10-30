package com.example.botdiscord.domain.usecase

import com.example.botdiscord.BotError
import com.example.botdiscord.domain.toDomain
import com.example.core.domain.Result
import com.example.core.util.dropFirstAndJoin
import com.example.wikiwavu.WavuWikiClient
import com.example.wikiwavu.domain.model.Move

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