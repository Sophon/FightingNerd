package io.github.sophon.dreamcancel.data.remote

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.dreamcancel.data.DreamCancelTables
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSource
import io.github.sophon.dreamcancel.data.toDomain

/**
 * DreamCancel returns all characters and their moves in a single remote call.
 * Both the character and move remote adapters share this cache so a full
 * refresh downloads once and every per-character move lookup is a cache hit.
 */
internal class DreamCancelDataCache(
    private val source: DreamCancelWikiDataSource,
    private val game: Game,
) {
    private val gameTables: QueryTable = DreamCancelTables.getTable(game.id)
        ?: error("${game.id} is not supported by DreamCancel.")

    private var cachedData: Map<Character, List<Move>>? = null

    suspend fun getOrFetch(): Result<Map<Character, List<Move>>, DataError> {
        val cached = cachedData
        if (cached != null) {
            val success = Result.Success(cached)
            return success
        }
        val result = source.downloadData(gameTables.moves)
            .flatMap { dto ->
                source.resolveHitboxUrls(dto).flatMap { hitboxUrlMap ->
                    source.resolveCharacterImageUrls(gameId = game.id, dto = dto)
                        .map { characterImageUrlMap ->
                            val map = dto.toDomain(
                                gameId = game.id,
                                imageUrlMap = hitboxUrlMap,
                                characterImageUrlMap = characterImageUrlMap,
                            )
                            map
                        }
                }
            }
            .onSuccess { cachedData = it }
        return result
    }

    fun clear() {
        cachedData = null
    }
}
