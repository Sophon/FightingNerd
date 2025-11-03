package io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onError
import io.github.sophon.cornerman.screens.home.HomeError
import io.github.sophon.cornerman.screens.home.toDomain
import io.github.sophon.wikiwavu.WavuWikiClient
import io.github.sophon.wikiwavu.domain.model.Character
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

//TODO: refactor - properly handle errors
@OptIn(ExperimentalCoroutinesApi::class)
internal class StartWavuSessionUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): EmptyResult<HomeError> {
        return when (val timestampResult = wiki.getLastUpdateTimeStamp()) {
            is Result.Success -> {
                when {
                    (timestampResult.data == null) -> {
                        Napier.d(tag = TAG) { "DB empty, filling database" }
                        fillDatabase()
                    }
                    timestampResult.data!!.isOld() -> {
                        Napier.d(tag = TAG) { "DB outdated, clearing and refilling" }
                        wiki.clearCache()
                        fillDatabase()
                    }
                    else -> {
                        Napier.d(tag = TAG) { "DB up to date" }
                        Result.Success(Unit)
                    }
                }
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "Error getting timestamp: ${timestampResult.error}" }
                Result.Error(timestampResult.error.toDomain())
            }
        }
    }

    private suspend fun fillDatabase(): EmptyResult<HomeError> {
        return when (val charListResult = wiki.downloadCharacterList()) {
            is Result.Success -> {
                val results = charListResult.data
                    .asFlow()
                    .flatMapMerge(concurrency = NUMBER_OF_CONCURRENT_REQUEST) { character ->
                        flow {
                            val result = downloadAndCacheForCharacter(character)
                            emit(result)
                        }
                    }
                    .toList()

                val errors = results.filterIsInstance<Result.Error<HomeError>>()

                if (errors.isNotEmpty()) {
                    Napier.e(tag = TAG) { "Failed to cache ${errors.size}/${charListResult.data.size} characters" }
                    errors.first() // Return first error
                } else {
                    Napier.d(tag = TAG) { "Successfully cached all ${charListResult.data.size} characters" }
                    Result.Success(Unit)
                }
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "Error downloading character list: ${charListResult.error}" }
                Result.Error(charListResult.error.toDomain())
            }
        }
    }

    private suspend fun downloadAndCacheForCharacter(character: Character): EmptyResult<HomeError> {
        return wiki.downloadMoveListFor(charName = character.displayName)
            .mapError { it.toDomain() }
            .flatMap { moveList ->
                wiki.cacheMoveList(character, moveList)
                    .mapError { it.toDomain() }
            }
            .asEmptyDataResult()
            .onError { error ->
                Napier.e(tag = TAG) { "Error caching ${character.displayName}: $error" }
            }
    }


    private fun Instant.isOld(): Boolean {
        val now = Clock.System.now()
        val age = now - this
        return age >= UPDATING_PERIOD_HOURS.hours
    }

    private companion object {
        const val UPDATING_PERIOD_HOURS = 6
        const val NUMBER_OF_CONCURRENT_REQUEST = 5
        const val TAG = "StartWavuSessionUseCase"
    }
}