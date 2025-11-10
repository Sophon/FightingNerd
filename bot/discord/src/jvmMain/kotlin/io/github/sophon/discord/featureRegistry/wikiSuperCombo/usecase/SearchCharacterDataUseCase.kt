package io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.wikiSuperCombo.SuperComboWikiClient

internal class SearchCharacterDataUseCase(
    private val wiki: SuperComboWikiClient,
) {
    suspend fun invoke(charName: String): Result<Character, BotError> {
        return wiki.getCharacter(charName)
            .mapError { it.toDomainError() }
    }
}