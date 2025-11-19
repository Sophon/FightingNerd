package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

internal class FetchCharacterUseCase(
    private val db: CharacterListDB,
) {
    suspend fun invoke(charName: String): Result<Character, WikiError> {
        return db.fetchCharacterDataFor(charName)
    }
}