package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.util.dropFirstAndJoin
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError

internal class GetMoveUseCase() {
    suspend fun invoke(
        wiki: WikiClient,
        query: String
    ): Result<Move, BotError> {
        val parsedQuery = query.parseQuery()
            ?: return Result.Error(BotError.UnknownMove(query))

        return wiki.fetchMove(
            charName = parsedQuery.charName,
            moveQuery = parsedQuery.move.replace(" ", "")
        ).mapError { it.toDomainError() }
    }

    internal data class ParsedQuery(
        val charName: String,
        val move: String,
    )

    internal fun String.parseQuery(): ParsedQuery? {
        if (split(" ").size < 2) return null

        val charName = substringBefore(' ')
        val move = dropFirstAndJoin(' ')

        return ParsedQuery(charName, move)
    }
}
