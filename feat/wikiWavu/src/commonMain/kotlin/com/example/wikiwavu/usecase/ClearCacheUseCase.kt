package com.example.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB

class ClearCacheUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(): EmptyResult<WavuError> = db.wipe()
}