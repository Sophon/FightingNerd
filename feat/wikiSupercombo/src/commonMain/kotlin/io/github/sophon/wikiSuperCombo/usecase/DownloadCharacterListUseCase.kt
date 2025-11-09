package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.toDomain

internal class DownloadCharacterListUseCase(
    private val source: SuperComboDataSource
) {
    suspend fun invoke(): Result<List<Character>, WikiError> {
        return source.downloadCharacterList()
            .map { dto ->  dto.toDomain() }
            .mapError { it.toDomainError(TAG) }
    }

    private companion object {
        const val TAG = "DownloadCharacterListUseCase"
    }
}