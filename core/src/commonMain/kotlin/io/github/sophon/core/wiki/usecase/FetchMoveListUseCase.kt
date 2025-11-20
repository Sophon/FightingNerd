package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

class FetchMoveListUseCase(
    private val fetch: suspend (String) -> Result<List<Move>, WikiError>
) {
    suspend fun invoke(charName: String): Result<List<Move>, WikiError> {
        return fetch(charName)
    }
}