package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move

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