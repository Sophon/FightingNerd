package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class GetMovesUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        characterId: String,
        filter: Filter = Filter.None,
    ): Result<Pair<Character, List<Move>>, BotError> {
        val result = wiki.fetchCharacter(characterQuery = characterId)
            .flatMap { character ->
                wiki.fetchMoveList(character.id, filter)
                    .map { moveList -> character to moveList.distinctBy { it.input } }
            }
            .mapError { it.toDomainError() }
        return result
    }
}