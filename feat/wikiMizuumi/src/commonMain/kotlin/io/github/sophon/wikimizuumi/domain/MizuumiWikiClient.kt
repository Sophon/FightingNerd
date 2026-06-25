package io.github.sophon.wikimizuumi.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.usecase.CachedDownloadUseCase
import io.github.sophon.wikimizuumi.data.MizuumiTables
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSource
import io.github.sophon.wikimizuumi.data.toDomain
import io.github.sophon.wikimizuumi.data.toDomainAll
import io.github.sophon.wikimizuumi.integration.MizuumiFeatureInfo
import io.github.sophon.wikimizuumi.integration.model.MBFilters
import io.github.sophon.wikimizuumi.integration.model.UniFilters
import io.github.sophon.wikimizuumi.integration.model.VSAVFilters
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class MizuumiWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    private val source: MizuumiWikiDataSource,
): BaseWikiClient(
    game = game,
    characterDB = characterDB,
    moveDB = moveDB,
    featureInfo = MizuumiFeatureInfo.featureInfo,
) {
    private val gameTables: QueryTable = MizuumiTables.getTable(game.id)
        ?: error("${game.id} not supported. Supported: $featureInfo")

    private val cachedDownloadUseCase = CachedDownloadUseCase { _ ->
        source.downloadData(gameTables.moves)
            .flatMap { dto ->
                source.resolveHitboxUrls(dto)
                    .flatMap { hitboxUrlMap ->
                        source.resolveCharacterImageUrlsFromMoveList(gameId = game.id, dto = dto)
                            .map { imageUrlMap ->
                                dto.toDomainAll(
                                    gameId = game.id,
                                    imageUrlMap = imageUrlMap,
                                    hitboxUrlMap = hitboxUrlMap,
                                )
                            }
                    }
            }
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = if (game.separateCharMoveDownload) {
            source.downloadCharacterList(gameTables.character)
                .flatMap { dto ->
                    source.resolveCharacterImageUrls(dto).map { imageUrlMap ->
                        val characterList = dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap)
                        Napier.i(tag = TAG) { "${game.id}: ${characterList.size} downloaded" }
                        characterList
                    }
                }
                .mapError { it.toDomainError() }
        } else {
            cachedDownloadUseCase.invoke()
                .map { map ->
                    val characterList = map.keys.toList()
                    Napier.i(tag = TAG) { "${game.id}: ${characterList.size} downloaded" }
                    characterList
                }
        }
        return result
    }

    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
        val result = if (game.separateCharMoveDownload) {
            cachedDownloadUseCase.invoke().map { map ->
                val moveList = map
                    .filterKeys { it.remoteQueryId == character.remoteQueryId }
                    .values
                    .flatten()
                Napier.d(tag = TAG) { "${character.id} (${game.id}): ${moveList.size} moves downloaded" }
                moveList
            }
        } else {
            source.downloadMoveList(table = gameTables.moves, character = character)
                .flatMap { dto ->
                    source.resolveHitboxUrls(dto).map { imageUrlMap ->
                        val moveList = dto.toDomain(
                            character = character,
                            gameId = game.id,
                            imageUrlMap = imageUrlMap,
                        )
                        Napier.d(tag = TAG) { "${character.id} (${game.id}): ${moveList.size} moves downloaded" }
                        moveList
                    }
                }
                .mapError { it.toDomainError() }
        }
        return result
    }

    override suspend fun onClearCache() {
        cachedDownloadUseCase.clearCache()
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in featureInfo.supportedGameSet) {
            "${game.id} not supported. Supported: $featureInfo"
        }

        val set = when (game) {
            Game.MBTL -> MBFilters.getAllBinaryFilters()
            Game.Uni2 -> UniFilters.getAllBinaryFilters()
            Game.VSAV -> VSAVFilters.getAllBinaryFilters()
            else -> emptySet()
        }

        return set
    }


    private companion object {
        const val TAG = "MizuumiWikiClient"
    }
}