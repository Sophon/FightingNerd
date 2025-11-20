package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

class FetchCharacterListUseCase(
    private val fetch: suspend () -> Result<List<Character>, WikiError>
) {
    suspend fun invoke(): Result<List<Character>, WikiError> {
        return fetch()
    }
}