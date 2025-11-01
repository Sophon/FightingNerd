package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.MoveListDB
import kotlinx.datetime.Instant

class GetLastCacheInsertInstantUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(): Result<Instant?, WavuError> = db.getLastInsertTimeStamp()
}