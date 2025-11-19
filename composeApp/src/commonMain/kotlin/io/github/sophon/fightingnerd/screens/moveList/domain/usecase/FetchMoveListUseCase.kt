package io.github.sophon.fightingnerd.screens.moveList.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.fightingnerd.screens.moveList.domain.MoveCategory
import io.github.sophon.fightingnerd.screens.moveList.domain.MoveListError
import io.github.sophon.fightingnerd.screens.moveList.domain.toDomain
import io.github.sophon.fightingnerd.screens.moveList.domain.toDomainError

internal class FetchMoveListUseCase {
    suspend fun invoke(wiki: WikiClient, charName: String): Result<List<MoveCategory>, MoveListError> {
        return wiki.fetchMoveList(charName)
            .map { it.toDomain() }
            .mapError { it.toDomainError() }
    }
}