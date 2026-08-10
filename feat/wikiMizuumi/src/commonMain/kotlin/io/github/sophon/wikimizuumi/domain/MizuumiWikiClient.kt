package io.github.sophon.wikimizuumi.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikimizuumi.data.MizuumiTables
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSource
import io.github.sophon.wikimizuumi.data.remote.MizuumiDataCache
import io.github.sophon.wikimizuumi.data.toDomain
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
    private val dataCache: MizuumiDataCache,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
): BaseWikiClient(
    game = game,
    characterDB = characterDB,
    moveDB = moveDB,
    featureInfo = MizuumiFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
    private val gameTables: QueryTable = MizuumiTables.getTable(game.id)
        ?: error("${game.id} not supported. Supported: $featureInfo")

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = if (game.separateCharMoveDownload) {
            source.downloadCharacterList(gameTables.character)
                .flatMap { dto ->
                    source.resolveCharacterImageUrls(dto).map { imageUrlMap ->
                        val characterList = dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap)
                        characterList
                    }
                }
                .mapError { it.toDomainError() }
        } else {
            dataCache.getOrFetch()
                .map { map ->
                    val characterList = map.keys.toList()
                    characterList
                }
                .mapError { it.toDomainError() }
        }
        return result
    }

    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
        val result = if (game.separateCharMoveDownload) {
            dataCache.getOrFetch()
                .map { map ->
                    val moveList = map
                        .filterKeys { it.remoteQueryId == character.remoteQueryId }
                        .values
                        .flatten()
                    moveList
                }
                .mapError { it.toDomainError() }
        } else {
            source.downloadMoveList(table = gameTables.moves, character = character)
                .flatMap { dto ->
                    source.resolveHitboxUrls(dto).map { hitboxUrlMap ->
                        val moveList = dto.cargoquery.map {
                            it.title.toDomain(character = character, hitboxUrlMap = hitboxUrlMap)
                        }
                        moveList
                    }
                }
                .mapError { it.toDomainError() }
        }
        return result
    }

    override suspend fun onClearCache() {
        dataCache.clear()
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
