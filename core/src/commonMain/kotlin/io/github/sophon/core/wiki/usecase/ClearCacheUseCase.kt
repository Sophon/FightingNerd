package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.wiki.data.WikiError

class ClearCacheUseCase(
    private val wipe: suspend () -> EmptyResult<WikiError>
) {
    suspend fun invoke(): EmptyResult<WikiError> = wipe()
}