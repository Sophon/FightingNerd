package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.toDomain

internal class DownloadCharacterListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(): Result<List<Character>, WikiError> {
        return source.downloadCharacterList()
            .map { dto -> dto.toDomain() }
            .mapError { it.toDomainError(TAG) }
    }

    private companion object {
        const val TAG = "DownloadCharacterListUseCase"
    }
}