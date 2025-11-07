package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError

class ClearCacheUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(): EmptyResult<WikiError> = db.wipe()
}