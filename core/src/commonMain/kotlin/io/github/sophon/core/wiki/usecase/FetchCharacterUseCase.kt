package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

class FetchCharacterUseCase(
    private val fetch: suspend (String) -> Result<Character, WikiError>,
) {
    suspend fun invoke(charName: String): Result<Character, WikiError> {
        return fetch(charName.lowercase())
    }
}
