package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.parseQuery

internal class GetMoveUseCase() {
    suspend fun invoke(
        wiki: WikiClient,
        query: String
    ): Result<Move, BotError> {
        val parsedQuery = query.parseQuery()
        if (parsedQuery == null) return Result.Error(BotError.UnknownMove(query))

        return wiki.fetchMove(
            charName = parsedQuery.charName, moveQuery = parsedQuery.move
        )
            .mapError { it.toDomainError() }
    }
}