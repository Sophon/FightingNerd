package io.github.sophon.wikiwavu.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.domain.model.Move
import kotlinx.datetime.Instant

interface MoveListDB {
    suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WavuError>
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
    suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WavuError>
    suspend fun wipe(): EmptyResult<WavuError>
    suspend fun getLastInsertTimeStamp(): Result<Instant?, WavuError>
}