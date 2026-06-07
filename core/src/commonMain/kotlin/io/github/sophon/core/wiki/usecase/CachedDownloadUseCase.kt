package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

/**
 * Use this usecase for Wikis that don't allow individual charList and moveList downloads
 */
class CachedDownloadUseCase(
    private val downloadAndMap: suspend (
        queryTable: QueryTable?
    ) -> Result<Map<Character, List<Move>>, DataError.Remote>
) {
    private var cachedData: Map<Character, List<Move>>? = null

    suspend fun invoke(
        queryTable: QueryTable? = null,
    ): Result<Map<Character, List<Move>>, WikiError> {
        return cachedData?.let { Result.Success(it) }
            ?: downloadAndMap.invoke(queryTable)
                .map {
                    cachedData = it
                    it
                }
                .mapError { it.toDomainError(TAG) }
    }

    fun clearCache() {
        cachedData = null
    }

    private companion object {
        const val TAG = "DownloadOrFetchUseCase"
    }
}