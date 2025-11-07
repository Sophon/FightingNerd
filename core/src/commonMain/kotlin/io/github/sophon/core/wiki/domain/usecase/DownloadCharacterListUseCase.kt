package io.github.sophon.core.wiki.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiDataSource
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.domain.model.Character

class DownloadCharacterListUseCase<C>(
    private val source: WikiDataSource<C, *>,
    private val toDomain: C.() -> List<Character>,
) {
    suspend fun invoke(): Result<List<Character>, WikiError> {
        return when (val result = source.downloadCharacterList()) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.error.toDomain(TAG))
        }
    }

    private companion object {
        const val TAG = "DownloadCharacterListUseCase"
    }
}