package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.toDomain

internal class DownloadMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WikiError> {
        return source.downloadMoveListFor(charName)
            .map { dto -> dto.toDomain(charName) }
            .mapError { it.toDomainError(TAG) }
    }

    private companion object {
        const val TAG = "DownloadMoveListUseCase"
    }
}