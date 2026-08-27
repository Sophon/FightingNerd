package io.github.sophon.wikimizuumi.data.remote

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

/**
 * Mizuumi wikis (per game) can return character-and-moves in a single bulk call.
 * Both remote adapters share this cache so a full refresh downloads once.
 */
internal class MizuumiDataCache(
    private val source: MizuumiWikiDataSource,
    private val game: Game,
) {
    private val gameTables: QueryTable = MizuumiTables.getTable(game.id)
        ?: error("${game.id} is not supported by Mizuumi.")

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
                    source.resolveCharacterImageUrlsFromMoveList(gameId = game.id, dto = dto)
                        .map { imageUrlMap ->
                            val map = dto.toDomainAll(
                                game = game,
                                imageUrlMap = imageUrlMap,
                                hitboxUrlMap = hitboxUrlMap,
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
