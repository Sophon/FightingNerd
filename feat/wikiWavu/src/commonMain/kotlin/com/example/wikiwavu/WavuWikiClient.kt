package com.example.wikiwavu

import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.core.domain.map
import com.example.core.domain.onError
import com.example.wikiwavu.domain.Scheduler
import com.example.wikiwavu.domain.model.Character
import com.example.wikiwavu.domain.model.CharacterMoveList
import com.example.wikiwavu.domain.model.Move
import com.example.wikiwavu.usecase.CacheMoveListUseCase
import com.example.wikiwavu.usecase.DownloadMoveListUseCase
import com.example.wikiwavu.usecase.DownloadCharacterListUseCase
import com.example.wikiwavu.usecase.FetchMoveDataUseCase
import com.example.wikiwavu.usecase.FetchMovesWithPropertyUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

interface WavuWikiClient {
    suspend fun startSession()
    suspend fun frameDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
    suspend fun getPowerCrushMoves(charName: String): Result<List<Move>, WavuError>
    suspend fun getHeatMoves(charName: String): Result<List<Move>, WavuError>
    suspend fun getHomingMoves(charName: String): Result<List<Move>, WavuError>
    suspend fun getMoveListFor(character: Character): Result<CharacterMoveList, WavuError>
    suspend fun getCharacterList(): Result<List<Character>, WavuError>
}

internal class WavuWikiClientImpl(
    private val downloadCharacterListUseCase: DownloadCharacterListUseCase,
    private val downloadMoveListUseCase: DownloadMoveListUseCase,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val fetchMoveDataUseCase: FetchMoveDataUseCase,
    private val fetchMovesWithPropertyUseCase: FetchMovesWithPropertyUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): WavuWikiClient {

    override suspend fun startSession() {
        scope.launch {
            scheduler.start(
                period = 1.hours,
                task = ::downloadCompleteMoveList,
            ).collect {
                it.onError { error ->
                    Napier.e(tag = TAG) { error.toString() }
                }
            }
        }
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
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.isPowerCrush }
    }

    override suspend fun getHeatMoves(
        charName: String
    ): Result<List<Move>, WavuError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.isHeat }
    }

    override suspend fun getHomingMoves(
        charName: String
    ): Result<List<Move>, WavuError> {
        return fetchMovesWithPropertyUseCase.invoke(charName) { it.isHoming }
    }

    override suspend fun getMoveListFor(character: Character): Result<CharacterMoveList, WavuError> {
        return when (val result = downloadMoveListUseCase.invoke(character)) {
            is Result.Success -> {
                Result.Success(result.data)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "Error: ${result.error} for $character" }
                Result.Error(result.error)
            }
        }
    }

    override suspend fun getCharacterList(): Result<List<Character>, WavuError> {
        return downloadCharacterListUseCase.invoke().map { it.characterList }
    }


    private suspend fun downloadCompleteMoveList(): EmptyResult<WavuError> {
        return when (val result = getCharacterList()) {
            is Result.Success -> {
                for (character in result.data) {
                    when (val moveListResult = downloadMoveListUseCase.invoke(character)) {
                        is Result.Success -> {
                            cacheMoveListUseCase.invoke(characterMoveList = moveListResult.data)
                            Napier.d(tag = TAG) {
                                "${moveListResult.data.moveList.size} moves for ${character.name} (${character.alias}) added"
                            }
                        }
                        is Result.Error -> {
                            Napier.e(tag = TAG) { "Error: ${moveListResult.error} for $character" }
                            return Result.Error(moveListResult.error)
                        }
                    }
                }

                Result.Success(Unit)
            }
            is Result.Error -> {
                Result.Error(result.error)
            }
        }
    }
}


private const val TAG = "com.example.wikiWavu.WavuWikiClient"