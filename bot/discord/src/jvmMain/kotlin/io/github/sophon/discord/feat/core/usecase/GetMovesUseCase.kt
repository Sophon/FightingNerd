package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.util.findMatching
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlinx.coroutines.flow.first

internal class GetMovesUseCase {
    suspend operator fun invoke(
        wiki: WikiClient,
        characterQuery: String,
        filter: Filter = Filter.None,
    ): Result<Pair<Character, List<Move>>, BotError> {
        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(characterQuery)
            ?: return Result.Error(BotError.UnknownCharacter(characterQuery))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
            .filter(filter.predicate)
            .distinctBy { it.input }

        val result = Result.Success(character to moveList)
        return result
    }
}
