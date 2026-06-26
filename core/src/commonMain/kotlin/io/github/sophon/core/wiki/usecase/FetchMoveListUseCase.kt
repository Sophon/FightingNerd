package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move

class FetchMoveListUseCase(
    private val fetch: suspend (String) -> Result<List<Move>, WikiError>
) {
    suspend fun invoke(
        charName: String,
        filter: Filter,
    ): Result<List<Move>, WikiError> {
        return fetch(charName.lowercase())
            .map { it.filter(filter.predicate) }
    }
}
