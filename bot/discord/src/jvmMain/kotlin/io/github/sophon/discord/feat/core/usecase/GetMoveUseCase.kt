package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.util.dropFirstAndJoin
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class GetMoveUseCase() {
    suspend fun invoke(
        wiki: WikiClient,
        query: String
    ): Result<Move, BotError> {
        val parsedQuery = query.parseQuery()
            ?: return Result.Error(BotError.UnknownMove(query))

        return wiki.fetchMove(
            characterQuery = parsedQuery.charName,
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
