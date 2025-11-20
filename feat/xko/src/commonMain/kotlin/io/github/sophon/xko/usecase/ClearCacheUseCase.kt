package io.github.sophon.xko.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError

internal class ClearCacheUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(): EmptyResult<WikiError> = db.wipe()
}