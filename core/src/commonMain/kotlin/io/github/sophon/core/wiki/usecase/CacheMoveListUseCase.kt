package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

class CacheMoveListUseCase(
    private val cache: suspend (Character, List<Move>) -> EmptyResult<WikiError>
) {
    suspend fun invoke(
        character: Character,
        moveList: List<Move>
    ): EmptyResult<WikiError> {
        return cache(character, moveList)
    }
}