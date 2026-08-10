package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.util.findMatching
import kotlinx.coroutines.flow.first

internal class GetCharacterUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        charName: String
    ): Result<Pair<Character, List<Move>>, BotError> {
        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(charName)
            ?: return Result.Error(BotError.UnknownCharacter(charName))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
        val normals = moveList.filter { it.isNormal() }
        val fastest = normals
            .groupBy { it.startup?.toIntOrNull() }
            .minByOrNull { it.key ?: Int.MAX_VALUE }
            ?.value
            ?: emptyList()

        val result = Result.Success(character to fastest)
        return result
    }

    private fun Move.isNormal(): Boolean {
        return input.first() in setOf('5', '2')
                && input.getOrNull(1)?.isDigit() == false
    }
}
