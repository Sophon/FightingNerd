package io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.fightingnerd.screens.home.HomeError
import io.github.sophon.fightingnerd.screens.home.toDomainError
import io.github.sophon.wikiSuperCombo.SuperComboWikiClient

internal class FetchCharacterListUseCase(
    private val wiki: SuperComboWikiClient,
) {
    suspend fun invoke(): Result<List<Character>, HomeError> {
        return wiki.getCharacterList().mapError { it.toDomainError() }
    }
}