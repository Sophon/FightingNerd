package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.dropFirstAndJoin
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.util.findMatching
import kotlinx.coroutines.flow.first

@ExcludeFromCoverage("plain client call")
internal class GetMoveUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        query: String
    ): Result<Pair<Character, Move>, BotError> {
        val parsedQuery = query.parseQuery()
            ?: return Result.Error(BotError.BotLogicError(query))

        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(parsedQuery.characterQuery)
            ?: return Result.Error(BotError.UnknownCharacter(parsedQuery.characterQuery))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
        val move = moveList.findMatching(parsedQuery.moveQuery)
            ?: return Result.Error(
                BotError.UnknownMove(parsedQuery.characterQuery, parsedQuery.moveQuery)
            )

        val result = Result.Success(character to move)
        return result
    }

    private fun List<Move>.findMatching(query: String): Move? {
        val normalizedQuery = query.normalizeForMatch()

        firstOrNull { it.id == normalizedQuery }
            ?.let { return it }
        firstOrNull { it.input.normalizeForMatch() == normalizedQuery }
            ?.let { return it }
        firstOrNull { move ->
            move.aliases.any { it.normalizeForMatch() == normalizedQuery }
        }?.let { return it }

        firstOrNull { move ->
            move.name?.normalizeForMatch() == normalizedQuery
        }?.let { return it }

        return null
    }

    private fun String.normalizeForMatch(): String {
        val normalized = replace(" ", "").lowercase()
        return normalized
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
