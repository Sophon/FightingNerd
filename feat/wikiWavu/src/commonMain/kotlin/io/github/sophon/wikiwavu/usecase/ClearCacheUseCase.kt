package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.MoveListDB

class ClearCacheUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(): EmptyResult<WavuError> = db.wipe()
}