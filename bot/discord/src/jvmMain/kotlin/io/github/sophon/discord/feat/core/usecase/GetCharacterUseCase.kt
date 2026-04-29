package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class GetCharacterUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        charName: String
    ): Result<Pair<Character, List<Move>>, BotError> {
        return wiki.fetchCharacter(charName)
            .mapError { it.toDomainError() }
            .flatMap { character ->
                wiki.fetchMoveList(character.id)
                    .mapError { it.toDomainError() }
                    .map { moveList ->
                        val normals = moveList.filter { it.isNormal() }
                        val fastest = normals
                            .groupBy { it.startup?.toIntOrNull() }
                            .minByOrNull { it.key ?: Int.MAX_VALUE }
                            ?.value
                            ?: emptyList()

                        character to fastest
                    }
            }
    }

    private fun Move.isNormal(): Boolean {
        return input.first() in setOf('5', '2')
                && input.getOrNull(1)?.isDigit() == false
    }
}