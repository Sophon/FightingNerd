package io.github.sophon.core.wiki.domain.usecase

import io.github.sophon.core.wiki.data.WikiDataSource
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Error
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character

class DownloadCharacterListUseCase<C, E: Error>(
    private val source: WikiDataSource<C, *>,
    private val toDomain: C.() -> List<Character>,
    private val toDomainError: DataError.Remote.() -> E,
) {
    suspend fun invoke(): Result<List<Character>, E> {
        return when (val result = source.downloadCharacterList()) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.error.toDomainError())
        }
    }
}