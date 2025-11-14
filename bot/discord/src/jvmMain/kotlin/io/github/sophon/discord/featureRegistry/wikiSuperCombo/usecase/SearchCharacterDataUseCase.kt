package io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.wikiSuperCombo.SuperComboWikiClient

internal class SearchCharacterDataUseCase(
    private val wiki: SuperComboWikiClient,
) {
    suspend fun invoke(
        charName: String
    ): Result<Pair<Character, List<Move>>, BotError> {
        return wiki.getCharacter(charName)
            .mapError { it.toDomainError() }
            .flatMap { character ->
                getFastestNormals(character.queryName)
                    .map { moveList ->
                        character to moveList
                    }
            }
    }

    private suspend fun getFastestNormals(
        charName: String
    ): Result<List<Move>, BotError> {
        return wiki.fetchMoveListFor(charName)
            .mapError { it.toDomainError() }
            .map { moveList ->
                val normals = moveList
                    .filter { move ->
                        val input = move.input.lowercase()
                        input.length == 3 && input.first() in setOf('5', '2')
                    }

                normals
                    .groupBy { it.startup?.toIntOrNull() }
                    .minByOrNull { it.key ?: Int.MAX_VALUE }
                    ?.value
                    ?: emptyList()
            }
    }
}