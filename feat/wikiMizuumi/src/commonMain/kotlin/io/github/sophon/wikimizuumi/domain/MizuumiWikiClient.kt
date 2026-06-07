package io.github.sophon.wikimizuumi.domain

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
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.usecase.CachedDownloadUseCase
import io.github.sophon.wikimizuumi.data.MizuumiTables
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSource
import io.github.sophon.wikimizuumi.data.toDomain
import io.github.sophon.wikimizuumi.data.toDomainAll
import io.github.sophon.wikimizuumi.integration.MizuumiFeatureInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class MizuumiWikiClient(
    private val game: Game,
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
        ?: error("${game.id} not supported. Supported: ${MizuumiFeatureInfo.featureInfo.supportedGameSet}")

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
                        dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap)
                    }
                }
                .mapError { it.toDomainError(TAG) }
        } else {
            cachedDownloadUseCase.invoke()
                .map { map -> map.keys.toList() }
        }
        return result
    }

    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
        val result = if (game.separateCharMoveDownload) {
            cachedDownloadUseCase.invoke().map { map ->
                map
                    .filterKeys { it.remoteQueryId == character.remoteQueryId }
                    .values
                    .flatten()
            }
        } else {
            source.downloadMoveList(table = gameTables.moves, character = character)
                .flatMap { dto ->
                    source.resolveHitboxUrls(dto).map { imageUrlMap ->
                        dto.toDomain(character = character, gameId = game.id, imageUrlMap = imageUrlMap)
                    }
                }
                .mapError { it.toDomainError(TAG) }
        }
        return result
    }

    override suspend fun onClearCache() {
        cachedDownloadUseCase.clearCache()
    }


    private companion object {
        const val TAG = "MizuumiWikiClient"
    }
}