package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

class FetchMoveUseCase(
    private val fetch: suspend (String, String) -> Result<Move, WikiError>
) {
    suspend fun invoke(
        charName: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return fetch(charName.lowercase(), moveQuery.lowercase())
    }
}
