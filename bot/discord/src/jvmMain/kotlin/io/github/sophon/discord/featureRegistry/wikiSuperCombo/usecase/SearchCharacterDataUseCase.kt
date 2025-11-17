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
                wiki.getFastestNormals(character.queryName)
                    .mapError { it.toDomainError() }
                    .map { moveList ->
                        character to moveList
                    }
            }
    }
}