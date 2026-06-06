package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Move

class FetchMoveUseCase(
    private val fetch: suspend (String, String) -> Result<Move, WikiError>
) {
    suspend fun invoke(
        characterId: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return fetch(characterId.lowercase(), moveQuery.lowercase())
    }
}
