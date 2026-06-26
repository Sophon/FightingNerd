package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character

class CacheCharacterListUseCase(
    private val cache: suspend (List<Character>) -> EmptyResult<WikiError>
) {
    suspend fun invoke(characterList: List<Character>): EmptyResult<WikiError> {
        return cache(characterList)
    }
}