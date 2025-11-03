package io.github.sophon.botdiscord.domain.usecase

import io.github.sophon.botdiscord.BotError
import io.github.sophon.botdiscord.domain.toDomain
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.mapError
import io.github.sophon.wikiwavu.WavuWikiClient
import io.github.sophon.wikiwavu.domain.model.Character
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadMoveListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): EmptyResult<BotError> {
        return when (val charListResult = wiki.downloadCharacterList()) {
            is Result.Success -> {
                charListResult.data
                    .asFlow()
                    .flatMapMerge(concurrency = NUMBER_OF_CONCURRENT_REQUEST) { character ->
                        flow {
                            val result = downloadAndCacheMove(character)
                            emit(result)
                        }
                    }
                    .firstOrNull { it is Result.Error }
                    ?: Result.Success(Unit)
            }
            is Result.Error -> Result.Error(charListResult.error.toDomain())
        }
    }

    private suspend fun downloadAndCacheMove(character: Character): EmptyResult<BotError> {
        return wiki.downloadMoveListFor(character.wikiName)
            .mapError { it.toDomain() }
            .flatMap { moveList ->
                wiki.cacheMoveList(character, moveList)
                    .mapError { it.toDomain() }
            }
            .asEmptyDataResult()
    }


    companion object {
        const val NUMBER_OF_CONCURRENT_REQUEST = 5
    }
}