package io.github.sophon.xko.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

internal class CacheCharacterListUseCase(
    private val db: CharacterListDB,
) {
    suspend fun invoke(characterList: List<Character>): EmptyResult<WikiError> {
        return db.insertCharacterList(characterList)
    }
}