package com.example.wikiwavu.data

import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.domain.model.Move
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface MoveListDB {
    suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WavuError>
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
    suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WavuError>
    suspend fun wipe(): EmptyResult<WavuError>
    @OptIn(ExperimentalTime::class) fun getLastInsertTimeStamp(): Result<Instant?, WavuError>
}