package io.github.sophon.xko.data.remote

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

/**
 * Xko returns all characters and their moves in a single remote call.
 * Both remote adapters share this cache so a full refresh downloads once.
 */
internal class XkoDataCache(
    private val source: XkoWikiDataSource,
) {
    private var cachedData: Map<Character, List<Move>>? = null

    suspend fun getOrFetch(): Result<Map<Character, List<Move>>, DataError> {
        val cached = cachedData
        if (cached != null) {
            val success = Result.Success(cached)
            return success
        }
        //TODO: resolve char images and hitboxes
        val result = source.downloadMoveList()
            .map { dto -> dto.toDomain() }
            .onSuccess { cachedData = it }
        return result
    }

    fun clear() {
        cachedData = null
    }
}
