package com.example.wikiwavu.usecase

import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GetLastCacheInsertInstantUseCase(
    private val db: MoveListDB,
) {
    fun invoke(): Result<Instant?, WavuError> = db.getLastInsertTimeStamp()
}