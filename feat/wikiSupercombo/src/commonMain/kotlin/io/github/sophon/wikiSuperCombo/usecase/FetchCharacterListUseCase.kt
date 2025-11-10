package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

internal class FetchCharacterListUseCase(
    private val db: CharacterListDB
) {
    suspend fun invoke(): Result<List<Character>, WikiError> {
        return when (val result = db.fetchCharacterList()) {
            is Result.Success -> {
                if (result.data.isEmpty()) {
                    Result.Error(WikiError.DATABASE_ERROR)
                } else {
                    Result.Success(result.data)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }
}