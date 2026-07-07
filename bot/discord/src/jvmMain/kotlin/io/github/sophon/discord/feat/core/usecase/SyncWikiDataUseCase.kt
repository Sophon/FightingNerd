package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.NUMBER_OF_CONCURRENT_REQUEST
import io.github.sophon.discord.REQUEST_TIMEOUT_S
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncWikiDataUseCase {
    suspend fun invoke(wikiList: Collection<WikiClient>): EmptyResult<BotError> {
        val errors = mutableListOf<BotError>()

        for (wiki in wikiList) {
            val characterListResult = downloadCharacterList(wiki)
            if (characterListResult == null) {
                errors.add(BotError.DownloadError("wiki host unresponsive — skipping remaining games"))
                break
            }

            val result = characterListResult
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
    ): Result<List<Character>, BotError>? {
        val result = withTimeoutOrNull(REQUEST_TIMEOUT_S.seconds) {
            wiki.downloadCharacterList()
                .mapError { it.toDomainError() }
        }
        return result
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
                    val timed = withTimeoutOrNull(REQUEST_TIMEOUT_S.seconds) {
                        wiki.downloadMoveListFor(character)
                            .map { moveList -> character to moveList }
                            .mapError { it.toDomainError() }
                    }
                    val result = timed ?: Result.Error(BotError.DownloadError("move list timeout"))
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
}