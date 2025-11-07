package io.github.sophon.wikiwavu

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.domain.onError
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.wiki.domain.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.data.MoveListResponseDto
import io.github.sophon.wikiwavu.usecase.CacheMoveListUseCase
import io.github.sophon.wikiwavu.usecase.ClearCacheUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveDataUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveListUseCase
import io.github.sophon.wikiwavu.usecase.FetchMovesWithPropertyUseCase
import io.github.sophon.wikiwavu.usecase.GetLastCacheInsertInstantUseCase
import kotlinx.datetime.Instant

interface WavuWikiClient {
    suspend fun downloadCharacterList(): Result<List<Character>, WikiError>
    suspend fun downloadMoveListFor(charName: String): Result<List<Move>, WikiError>
    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError>
    suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError>
    suspend fun clearCache(): EmptyResult<WikiError>

    suspend fun frameDataFor(charName: String, moveQuery: String): Result<Move, WikiError>
    suspend fun getPowerCrushMoves(charName: String): Result<List<Move>, WikiError>
    suspend fun getHeatMoves(charName: String): Result<List<Move>, WikiError>
    suspend fun getHomingMoves(charName: String): Result<List<Move>, WikiError>
    suspend fun getMoveListFor(charName: String): Result<List<Move>, WikiError>
    suspend fun getCharacterList(): Result<List<Character>, WikiError>
}

internal class WavuWikiClientImpl(
    private val downloadCharacterListUseCase: DownloadCharacterListUseCase<CharacterListResponseDto>,
    private val downloadMoveListUseCase: DownloadMoveListUseCase<MoveListResponseDto>,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val getLastCacheInsertInstantUseCase: GetLastCacheInsertInstantUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,

    private val fetchMoveDataUseCase: FetchMoveDataUseCase,
    private val fetchMovesWithPropertyUseCase: FetchMovesWithPropertyUseCase,
    private val fetchMoveListUseCase: FetchMoveListUseCase,
): WavuWikiClient {
    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = downloadCharacterListUseCase.invoke()
        when (result) {
            is Result.Success -> Napier.d(tag = TAG) { "${result.data.size} characters loaded" }
            is Result.Error -> Napier.e(tag = TAG) { result.error.toString() }
        }

        return result
    }

    override suspend fun downloadMoveListFor(charName: String): Result<List<Move>, WikiError> {
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

    override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> {
        val result = cacheMoveListUseCase.invoke(character, moveList)

        when (result) {
            is Result.Error -> Napier.d(tag = TAG) { "${character.id}: ${result.error}" }
            else -> {}
        }

        return result
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        return getLastCacheInsertInstantUseCase.invoke()
    }

    override suspend fun clearCache(): EmptyResult<WikiError> {
        return clearCacheUseCase.invoke()
    }

    override suspend fun frameDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        return fetchMoveDataUseCase.invoke(charName, moveQuery)
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun getPowerCrushMoves(
        charName: String
    ): Result<List<Move>, WikiError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.t8Properties?.isPowerCrush == true }
    }

    override suspend fun getHeatMoves(
        charName: String
    ): Result<List<Move>, WikiError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.t8Properties?.isHeat == true }
    }

    override suspend fun getHomingMoves(
        charName: String
    ): Result<List<Move>, WikiError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.t8Properties?.isHoming == true }
    }

    override suspend fun getMoveListFor(
        charName: String,
    ): Result<List<Move>, WikiError> {
        return fetchMoveListUseCase.invoke(charName)
    }

    override suspend fun getCharacterList(): Result<List<Character>, WikiError> {
        downloadCharacterListUseCase.invoke()
        TODO("this should be a DB function")
    }
}


private const val TAG = "WavuWikiClient"