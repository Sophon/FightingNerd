package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlinx.coroutines.flow.first

internal class GetMovesUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        characterId: String,
        filter: Filter = Filter.None,
    ): Result<Pair<Character, List<Move>>, BotError> {
        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.firstOrNull { it.id == characterId }
            ?: return Result.Error(BotError.UnknownCharacter(characterId))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
            .filter(filter.predicate)
            .distinctBy { it.input }

        val result = Result.Success(character to moveList)
        return result
    }
}
