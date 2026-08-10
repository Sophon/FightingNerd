package io.github.sophon.wikidragdown.domain

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
import io.github.sophon.wikidragdown.data.DragDownDataSource
import io.github.sophon.wikidragdown.data.DragDownTables
import io.github.sophon.wikidragdown.data.toDomain
import io.github.sophon.wikidragdown.integration.DragDownFeatureInfo

internal class DragDownWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    private val source: DragDownDataSource,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
): BaseWikiClient(
    game = game,
    characterDB = characterDB,
    moveDB = moveDB,
    featureInfo = DragDownFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
    private val gameTables: QueryTable = DragDownTables.getTable(game.id)
        ?: error("${game.id} not supported. Supported: $supportedGameSet")


    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = source.downloadCharacterList(table = gameTables.character)
            .flatMap { dto ->
                source.resolveCharacterImageUrls(dto).map { imageUrlMap ->
                    val characterList = dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap)
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
                        character = character,
                        imageUrlMap = imageUrlMap,
                    )
                    moveList
                }
            }
            .mapError { it.toDomainError() }
        return result
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in featureInfo.supportedGameSet) {
            "${game.id} not supported. Supported: ${featureInfo.supportedGameSet}"
        }

        return emptySet()
    }


    private companion object {
        const val TAG = "DragDownWikiClient"
    }
}
