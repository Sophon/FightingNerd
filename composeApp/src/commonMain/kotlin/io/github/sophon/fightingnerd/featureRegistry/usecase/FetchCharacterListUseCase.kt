package io.github.sophon.fightingnerd.featureRegistry.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.fightingnerd.feat.home.model.HomeError
import io.github.sophon.fightingnerd.feat.home.model.toDomainError

internal class FetchCharacterListUseCase {
    suspend fun invoke(wiki: WikiClient): Result<List<Character>, HomeError> {
        return wiki.fetchCharacterList()
            .mapError { it.toDomainError() }
    }
}