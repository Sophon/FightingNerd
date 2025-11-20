package io.github.sophon.xko.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.toDomain

internal class DownloadOrFetchUseCase(
    private val dataSource: XkoWikiDataSource,
) {
    private var cachedData: Map<Character, List<Move>>? = null

    suspend fun invoke(): Result<Map<Character, List<Move>>, WikiError> {
        return cachedData?.let { Result.Success(it) }
            ?: dataSource.downloadMoveList()
                .map { it.toDomain() }
                .mapError { it.toDomainError(TAG) }
                .onSuccess { cachedData = it }
    }

    fun clearCache() {
        cachedData = null
    }

    private companion object Companion {
        const val TAG = "DownloadOrFetchUseCase"
    }
}