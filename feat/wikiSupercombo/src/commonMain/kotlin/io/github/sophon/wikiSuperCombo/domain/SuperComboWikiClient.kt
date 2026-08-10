package io.github.sophon.wikiSuperCombo.domain

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
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboTables
import io.github.sophon.wikiSuperCombo.data.toDomain
import io.github.sophon.wikiSuperCombo.integration.SuperComboFeatureInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class SuperComboWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    private val source: SuperComboDataSource,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
): BaseWikiClient(
    game = game,
    characterDB = characterDB,
    moveDB = moveDB,
    featureInfo = SuperComboFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
    private val gameTables: QueryTable = SuperComboTables.getTable(game.id)
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
        val result = source.downloadMoveList(table = gameTables.moves, character)
            .flatMap { dto ->
                source.resolveHitboxUrls(dto).map { imageUrlMap ->
                    val moveList = dto.toDomain(
                        gameId = game.id,
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
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        return emptySet()
    }


    private companion object {
        const val TAG = "SuperComboWikiClient"
    }
}
