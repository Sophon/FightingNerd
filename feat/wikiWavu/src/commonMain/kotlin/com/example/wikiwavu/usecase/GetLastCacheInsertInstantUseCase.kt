package com.example.wikiwavu.usecase

import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import kotlinx.datetime.Instant

class GetLastCacheInsertInstantUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(): Result<Instant?, WavuError> = db.getLastInsertTimeStamp()
}