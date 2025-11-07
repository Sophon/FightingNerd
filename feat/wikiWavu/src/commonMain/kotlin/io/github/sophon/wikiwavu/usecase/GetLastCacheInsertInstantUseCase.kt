package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import kotlinx.datetime.Instant

class GetLastCacheInsertInstantUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(): Result<Instant?, WikiError> = db.getLastInsertTimeStamp()
}