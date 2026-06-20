package io.github.sophon.wikiwavu.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
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
import io.github.sophon.wikiwavu.data.WavuTables
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.toDomain
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import io.github.sophon.wikiwavu.integration.model.TekkenFilters
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class WavuWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    private val source: WavuWikiDataSource,
): BaseWikiClient(
    game = game,
    characterDB = characterDB,
    moveDB = moveDB,
    featureInfo = WavuFeatureInfo.featureInfo,
) {
    private val gameTables: QueryTable = WavuTables.getTable(game.id)
        ?: error("${game.id} not supported. Supported: $supportedGameSet")


    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = source.downloadCharacterList()
            .map { dto ->
                val characterList = dto.toDomain()
                Napier.i(tag = TAG) { "${game.id}: ${characterList.size} downloaded" }
                characterList
            }
            .mapError { it.toDomainError(TAG) }
        return result
    }

    override suspend fun downloadMoveListFor(
        character: Character,
    ): Result<List<Move>, WikiError> {
        val result = source.downloadMoveList(table = gameTables.moves, character = character)
            .map { dto ->
                val moveList = dto.toDomain(character)
                Napier.d(tag = TAG) { "${character.id} (${game.id}): ${moveList.size} moves downloaded" }
                moveList
            }
            .mapError { it.toDomainError(TAG) }
        return result
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        val filters = when (game) {
            Game.Tekken8 -> TekkenFilters.getAll()
            else -> emptySet()
        }
        return filters
    }


    private companion object {
        const val TAG = "WavuWikiClient"
    }
}
