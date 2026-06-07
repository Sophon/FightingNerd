package io.github.sophon.dreamcancel.domain

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.usecase.CachedDownloadUseCase
import io.github.sophon.dreamcancel.data.DreamCancelTables
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSource
import io.github.sophon.dreamcancel.data.toDomain
import io.github.sophon.dreamcancel.integration.DreamCancelFeatureInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class DreamCancelWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    private val source: DreamCancelWikiDataSource,
) : BaseWikiClient(
    game = game,
    featureInfo = DreamCancelFeatureInfo.featureInfo,
    characterDB = characterDB,
    moveDB = moveDB,
) {
    private val gameTables: QueryTable = DreamCancelTables.getTable(game.id)
        ?: error("${game.id} not supported. Supported: ${DreamCancelFeatureInfo.featureInfo.supportedGameSet}")

    private val cachedDownloadUseCase = CachedDownloadUseCase {
        source.downloadData(gameTables.moves)
            .flatMap { dto ->
                source.resolveHitboxUrls(dto)
                    .map { imageUrlMap -> dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap) }
            }
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = cachedDownloadUseCase.invoke().map { map ->
            map.keys.toList()
        }
        return result
    }

    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
        val result = cachedDownloadUseCase.invoke()
            .map { map ->
                map
                    .filterKeys { it.remoteQueryId == character.remoteQueryId }
                    .values
                    .flatten()
            }
        return result
    }

    override suspend fun onClearCache() {
        cachedDownloadUseCase.clearCache()
    }


    private companion object {
        const val TAG = "DreamCancelWikiClient"
    }
}
