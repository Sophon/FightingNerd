package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.dropFirstAndJoin
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.util.findMatching
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlinx.coroutines.flow.first

@ExcludeFromCoverage("plain client call")
internal class GetMoveUseCase {
    suspend operator fun invoke(
        wiki: WikiClient,
        query: String,
        sanitizeMoveInput: String.() -> String = { this },
    ): Result<Pair<Character, Move>, BotError> {
        val parsedQuery = query.parseQuery(sanitizeMoveInput)
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

    internal data class ParsedQuery(
        val characterQuery: String,
        val moveQuery: String,
    )

    internal fun String.parseQuery(sanitizeMoveInput: String.() -> String): ParsedQuery? {
        if (split(" ").size < 2) return null

        val charName = substringBefore(' ')
        val move = dropFirstAndJoin(' ').sanitizeMoveInput()

        return ParsedQuery(charName, move)
    }
}
