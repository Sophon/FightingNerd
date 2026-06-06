package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.data.WikiError

class CheckHasCachedMoveListUseCase(
    private val fetch: suspend (String) -> Result<Boolean, WikiError>
) {
    suspend fun invoke(characterId: String): Result<Boolean, WikiError> {
        return fetch(characterId)
    }
}