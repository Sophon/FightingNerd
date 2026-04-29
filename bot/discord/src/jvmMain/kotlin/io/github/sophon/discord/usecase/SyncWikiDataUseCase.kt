package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.toDomainError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncWikiDataUseCase {
    suspend fun invoke(wikiList: Collection<WikiClient>): EmptyResult<BotError> {
        val errors = mutableListOf<BotError>()

        wikiList.forEach { wiki ->
            val result = downloadCharacterList(wiki)
                .flatMap { characterList ->
                    downloadMoveLists(wiki, characterList)
                }
                .flatMap { characterMoveListPairList ->
                    wiki.clearCache()
                        .mapError { it.toDomainError() }
                        .flatMap {
                            val filteredPairList = characterMoveListPairList.filterOutCharsWithNoMoves()
                            cacheCharacterList(
                                wiki = wiki,
                                characterList = filteredPairList.map { it.first }
                            ).map { filteredPairList }
                        }
                }
                .flatMap { characterMoveListPairList ->
                    cacheMoveList(wiki, characterMoveListPairList)
                }

            if (result is Result.Error) {
                errors.add(result.error)
            }
        }

        return if (errors.isEmpty()) {
            Result.Success(Unit)
        } else {
            Result.Error(BotError.Unknown("TODO: syncing"))
        }
    }

    private suspend fun downloadCharacterList(
        wiki: WikiClient,
    ): Result<List<Character>, BotError> {
        return wiki.downloadCharacterList()
            .mapError { it.toDomainError() }
    }

    private suspend fun cacheCharacterList(
        wiki: WikiClient,
        characterList: List<Character>
    ): EmptyResult<BotError> {
        return wiki.cacheCharacterList(characterList)
            .mapError { it.toDomainError() }
    }

    private suspend fun downloadMoveLists(
        wiki: WikiClient,
        characterList: List<Character>,
    ): Result<List<Pair<Character, List<Move>>>, BotError> {
        val successfulDownloads = mutableListOf<Pair<Character, List<Move>>>()
        var firstError: Result.Error<BotError>? = null

        characterList.asFlow()
            .flatMapMerge(concurrency = NUMBER_OF_CONCURRENT_REQUEST) { character ->
                flow {
                    val data = DownloadMoveListUseCase.CharacterData(
                        name = character.queryName,
                        imageUrl = character.images?.iconUrl
                    )
                    val result = wiki.downloadMoveList(data)
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
        wiki: WikiClient,
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

    private fun List<Pair<Character, List<Move>>>.filterOutCharsWithNoMoves(): List<Pair<Character, List<Move>>> {
        return filter { it.second.isNotEmpty() }
    }


    private companion object Companion {
        const val NUMBER_OF_CONCURRENT_REQUEST = 5
    }
}