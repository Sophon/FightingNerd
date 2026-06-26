package io.github.sophon.wikidustloop.domain

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
import io.github.sophon.wikidustloop.data.DustLoopDataSource
import io.github.sophon.wikidustloop.data.DustLoopTables
import io.github.sophon.wikidustloop.data.toDomain
import io.github.sophon.wikidustloop.integration.DustLoopFeatureInfo
import io.github.sophon.wikidustloop.integration.model.BBFilters
import io.github.sophon.wikidustloop.integration.model.GGFilters
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class DustLoopWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    private val source: DustLoopDataSource,
): BaseWikiClient(
    game = game,
    characterDB = characterDB,
    moveDB = moveDB,
    featureInfo = DustLoopFeatureInfo.featureInfo,
) {
    private val gameTables: QueryTable = DustLoopTables.getTable(game.id)
        ?: error("${game.id} not supported. Supported: $supportedGameSet")


    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = source.downloadCharacterList(table = gameTables.character)
            .flatMap { dto ->
                source.resolveCharacterImageUrls(dto).map { imageUrlMap ->
                    val characterList = dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap)
                    Napier.i(tag = TAG) { "${game.id}: ${characterList.size} downloaded" }
                    characterList
                }
            }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
        val result = source.downloadMoveList(gameTables.moves, character)
            .flatMap { dto ->
                source.resolveHitboxUrls(dto).map { imageUrlMap ->
                    val moveList = dto.toDomain(
                        gameId = game.id,
                        character = character,
                        imageUrlMap = imageUrlMap,
                    )
                    Napier.d(tag = TAG) { "${character.id} (${game.id}): ${moveList.size} moves downloaded" }
                    moveList
                }
            }
            .mapError { it.toDomainError() }
        return result
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in DustLoopFeatureInfo.featureInfo.supportedGameSet) {
            "${game.id} not supported. Supported: ${DustLoopFeatureInfo.featureInfo.supportedGameSet}"
        }

        val set = when (game) {
            Game.BBCF -> BBFilters.getAllBinaryFilters()
            Game.GGST -> GGFilters.getAllBinaryFilters()
            else -> emptySet()
        }
        return set
    }


    private companion object {
        const val TAG = "DustLoopWikiClient"
    }
}