package io.github.sophon.wikiwavu

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.model.Character
import io.github.sophon.core.domain.model.Move
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.usecase.CacheMoveListUseCase
import io.github.sophon.wikiwavu.usecase.ClearCacheUseCase
import io.github.sophon.wikiwavu.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveDataUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveListUseCase
import io.github.sophon.wikiwavu.usecase.FetchMovesWithPropertyUseCase
import io.github.sophon.wikiwavu.usecase.GetLastCacheInsertInstantUseCase
import kotlinx.datetime.Instant

interface WavuWikiClient {
    suspend fun downloadCharacterList(): Result<List<Character>, WavuError>
    suspend fun downloadMoveListFor(charName: String): Result<List<Move>, WavuError>
    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WavuError>
    suspend fun getLastUpdateTimeStamp(): Result<Instant?, WavuError>
    suspend fun clearCache(): EmptyResult<WavuError>

    suspend fun frameDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
    suspend fun getPowerCrushMoves(charName: String): Result<List<Move>, WavuError>
    suspend fun getHeatMoves(charName: String): Result<List<Move>, WavuError>
    suspend fun getHomingMoves(charName: String): Result<List<Move>, WavuError>
    suspend fun getMoveListFor(charName: String): Result<List<Move>, WavuError>
    suspend fun getCharacterList(): Result<List<Character>, WavuError>
}

internal class WavuWikiClientImpl(
    private val downloadCharacterListUseCase: DownloadCharacterListUseCase<CharacterListResponseDto, WavuError>,
    private val downloadMoveListUseCase: DownloadMoveListUseCase,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val getLastCacheInsertInstantUseCase: GetLastCacheInsertInstantUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,

    private val fetchMoveDataUseCase: FetchMoveDataUseCase,
    private val fetchMovesWithPropertyUseCase: FetchMovesWithPropertyUseCase,
    private val fetchMoveListUseCase: FetchMoveListUseCase,
): WavuWikiClient {
    override suspend fun downloadCharacterList(): Result<List<Character>, WavuError> {
        val result = downloadCharacterListUseCase.invoke()
        when (result) {
            is Result.Success -> Napier.d(tag = TAG) { "${result.data.size} characters loaded" }
            is Result.Error -> Napier.e(tag = TAG) { result.error.toString() }
        }

        return result
    }

    override suspend fun downloadMoveListFor(charName: String): Result<List<Move>, WavuError> {
        return when (val result = downloadMoveListUseCase.invoke(charName)) {
            is Result.Success -> {
                Napier.d(tag = TAG) {
                    "${charName}: ${result.data.size} moves downloaded"
                }
                Result.Success(result.data)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "$charName: ${result.error}" }
                Result.Error(result.error)
            }
        }
    }

    override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WavuError> {
        val result = cacheMoveListUseCase.invoke(character, moveList)

        when (result) {
            is Result.Error -> Napier.d(tag = TAG) { "${character.id}: ${result.error}" }
            else -> {}
        }

        return result
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WavuError> {
        return getLastCacheInsertInstantUseCase.invoke()
    }

    override suspend fun clearCache(): EmptyResult<WavuError> {
        return clearCacheUseCase.invoke()
    }

    override suspend fun frameDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WavuError> {
        return fetchMoveDataUseCase.invoke(charName, moveQuery)
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun getPowerCrushMoves(
        charName: String
    ): Result<List<Move>, WavuError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.t8Properties?.isPowerCrush == true }
    }

    override suspend fun getHeatMoves(
        charName: String
    ): Result<List<Move>, WavuError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.t8Properties?.isHeat == true }
    }

    override suspend fun getHomingMoves(
        charName: String
    ): Result<List<Move>, WavuError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.t8Properties?.isHoming == true }
    }

    override suspend fun getMoveListFor(
        charName: String,
    ): Result<List<Move>, WavuError> {
        return fetchMoveListUseCase.invoke(charName)
    }

    override suspend fun getCharacterList(): Result<List<Character>, WavuError> {
        return downloadCharacterListUseCase.invoke()
    }
}


private const val TAG = "WavuWikiClient"