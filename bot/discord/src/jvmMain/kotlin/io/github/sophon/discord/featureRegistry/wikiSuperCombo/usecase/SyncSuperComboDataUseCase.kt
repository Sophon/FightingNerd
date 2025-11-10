package io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.wikiSuperCombo.SuperComboWikiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
class SyncSuperComboDataUseCase(
    private val wiki: SuperComboWikiClient,
) {
    suspend fun invoke(): EmptyResult<BotError> {
        wiki.clearCache()

        return downloadCharacterList()
            .flatMap { characterList ->
                cacheCharacterList(characterList)
                    .map { characterList }
            }
            .flatMap { characterList ->
                downloadMoveLists(characterList)
            }
            .flatMap { characterMoveListPairList ->
                cacheMoveList(characterMoveListPairList)
            }
    }

    private suspend fun downloadCharacterList(): Result<List<Character>, BotError> {
        return wiki.downloadCharacterList()
            .mapError { it.toDomainError() }
    }

    private suspend fun cacheCharacterList(
        characterList: List<Character>
    ): EmptyResult<BotError> {
        return wiki.cacheCharaterList(characterList)
            .mapError { it.toDomainError() }
    }

    private suspend fun downloadMoveLists(
        characterList: List<Character>,
    ): Result<List<Pair<Character, List<Move>>>, BotError> {
        val successfulDownloads = mutableListOf<Pair<Character, List<Move>>>()
        var firstError: Result.Error<BotError>? = null

        characterList.asFlow()
            .flatMapMerge(concurrency = NUMBER_OF_CONCURRENT_REQUEST) { character ->
                flow {
                    val result = wiki.downloadMoveListFor(character.queryName.orEmpty())
                        .map { moveList -> character to moveList }
                        .mapError { it.toDomainError() }
                    emit(result)
                }
            }
            .collect { result ->
                when (result) {
                    is Result.Success -> successfulDownloads.add(result.data)
                    is Result.Error -> if (firstError == null) firstError = result
                }
            }

        return firstError ?: Result.Success(successfulDownloads)
    }

    private suspend fun cacheMoveList(
        characterMoveListPairList: List<Pair<Character, List<Move>>>
    ): EmptyResult<BotError> {
        return characterMoveListPairList.asFlow()
            .flatMapMerge(concurrency = NUMBER_OF_CONCURRENT_REQUEST) { (character, moveList) ->
                flow {
                    val result = wiki.cacheMoveList(character, moveList)
                        .mapError { it.toDomainError() }
                    emit(result)
                }
            }
            .firstOrNull { it is Result.Error }
            ?: Result.Success(Unit)
    }


    private companion object Companion {
        const val NUMBER_OF_CONCURRENT_REQUEST = 5
    }
}