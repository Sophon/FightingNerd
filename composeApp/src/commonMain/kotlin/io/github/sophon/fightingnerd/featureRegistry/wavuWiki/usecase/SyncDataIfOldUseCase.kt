package io.github.sophon.fightingnerd.featureRegistry.wavuWiki.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.fightingnerd.screens.home.HomeError
import io.github.sophon.fightingnerd.screens.home.toDomainError
import io.github.sophon.wikiwavu.WavuWikiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncDataIfOldUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): EmptyResult<HomeError> {
        return when (val timestampResult = wiki.getLastUpdateTimeStamp()) {
            is Result.Success -> {
                val timestamp = timestampResult.data
                when {
                    timestamp == null -> {
                        Napier.d(tag = TAG) { "DB empty, syncing" }
                        syncData()
                    }
                    timestamp.isOld() -> {
                        Napier.d(tag = TAG) { "DB outdated, syncing" }
                        wiki.clearCache()
                        syncData()
                    }
                    else -> {
                        Napier.d(tag = TAG) { "DB up to date" }
                        Result.Success(Unit)
                    }
                }
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "Error getting timestamp: ${timestampResult.error}" }
                Result.Error(timestampResult.error.toDomainError())
            }
        }
    }

    private suspend fun syncData(): EmptyResult<HomeError> {
        return downloadCharacterList()
            .flatMap { characterList ->
                cacheCharacterList(characterList)
                    .map { characterList }
            }
            .flatMap { characterList ->
                downloadMoveLists(characterList)
            }
            .flatMap { characterMoveListPairList ->
                cacheMoveLists(characterMoveListPairList)
            }
    }

    private suspend fun downloadCharacterList(): Result<List<Character>, HomeError> {
        return wiki.downloadCharacterList()
            .mapError { it.toDomainError() }
    }

    private suspend fun cacheCharacterList(
        characterList: List<Character>,
    ): EmptyResult<HomeError> {
        return wiki.cacheCharacterList(characterList)
            .mapError { it.toDomainError() }
    }

    private suspend fun downloadMoveLists(
        characterList: List<Character>,
    ): Result<List<Pair<Character, List<Move>>>, HomeError> {
        val successfulDownloads = mutableListOf<Pair<Character, List<Move>>>()
        var firstError: Result.Error<HomeError>? = null

        characterList
            .asFlow()
            .flatMapMerge(concurrency = NUMBER_OF_CONCURRENT_REQUEST) { character ->
                flow {
                    val result = wiki.downloadMoveListFor(character.displayName)
                        .mapError { it.toDomainError() }
                        .map { moveList -> character to moveList }
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

    private suspend fun cacheMoveLists(
        characterMoveListPairList: List<Pair<Character, List<Move>>>
    ): EmptyResult<HomeError> {
        return characterMoveListPairList
            .asFlow()
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


    private fun Instant.isOld(): Boolean {
        val now = Clock.System.now()
        val age = now - this
        return age >= UPDATING_PERIOD_HOURS.hours
    }

    private companion object {
        const val UPDATING_PERIOD_HOURS = 6
        const val NUMBER_OF_CONCURRENT_REQUEST = 5
        const val TAG = "SyncDataIfOldUseCase"
    }
}