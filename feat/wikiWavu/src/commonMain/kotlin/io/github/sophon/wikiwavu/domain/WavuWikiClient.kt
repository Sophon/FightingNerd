package io.github.sophon.wikiwavu.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
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
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.data.remote.WavuTables
import io.github.sophon.wikiwavu.data.remote.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.remote.toDomain
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import io.github.sophon.wikiwavu.integration.model.TekkenFilters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class WavuWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    private val source: WavuWikiDataSource,
    private val characterRepo: CharacterRepo,
    private val moveRepo: MoveRepo,
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
            .mapError { it.toDomainError() }
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
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun refreshData(): EmptyResult<WikiError> {
        //TODO: handle per-request timeout and host-unresponsive short-circuit
        val charResult = characterRepo.refreshCharacterList()
        if (charResult is Result.Error) {
            val error = Result.Error(charResult.error.toDomainError())
            return error
        }

        val characterList = characterRepo.subscribeToCharacterList().first()
        Napier.i(tag = TAG) { "${game.id}: ${characterList.size} downloaded" }

        for (character in characterList) {
            moveRepo.refreshMoveList(character)
                .onSuccess { moveListSize ->
                    Napier.d(tag = TAG) { "${character.id} (${game.id}): $moveListSize moves downloaded" }
                }
                .onError {
                    val error = Result.Error(it.toDomainError())
                    return error
                }
        }
        val result = Result.Success(Unit)
        return result
    }

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        val flow = characterRepo.subscribeToCharacterList()
        return flow
    }

    override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> {
        val flow = moveRepo.subscribeToMoveList(characterId.value)
        return flow
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        val filters = when (game) {
            Game.Tekken8 -> TekkenFilters.getAllBinaryFilters()
            else -> emptySet()
        }
        return filters
    }

    override suspend fun fetchMove(
        characterId: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        val cleanedMoveQuery = moveQuery.cleanMoveInput(keepSpaces = true)
        return super.fetchMove(characterId, cleanedMoveQuery)
    }

    private companion object {
        const val TAG = "WavuWikiClient"
    }
}
