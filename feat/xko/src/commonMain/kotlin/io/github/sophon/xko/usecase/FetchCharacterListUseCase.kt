package io.github.sophon.xko.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

internal class FetchCharacterListUseCase(
    private val db: CharacterListDB,
) {
    suspend fun invoke(): Result<List<Character>, WikiError> {
        return db.fetchCharacterList()
    }
}