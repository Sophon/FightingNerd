package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.util.dropFirstAndJoin
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class GetMoveUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        query: String
    ): Result<Pair<Character, Move>, BotError> {
        val parsedQuery = query.parseQuery()
            ?: return Result.Error(BotError.BotLogicError(query))

        val result = wiki.fetchCharacter(characterQuery = parsedQuery.characterQuery)
            .flatMap { character ->
                wiki.fetchMove(
                    characterId = character.id,
                    moveQuery = parsedQuery.moveQuery.replace(" ", ""),
                ).map { move -> Pair(character, move) }
            }
            .mapError { it.toDomainError() }

        return result
    }

    internal data class ParsedQuery(
        val characterQuery: String,
        val moveQuery: String,
    )

    internal fun String.parseQuery(): ParsedQuery? {
        if (split(" ").size < 2) return null

        val charName = substringBefore(' ')
        val move = dropFirstAndJoin(' ')

        return ParsedQuery(charName, move)
    }
}
