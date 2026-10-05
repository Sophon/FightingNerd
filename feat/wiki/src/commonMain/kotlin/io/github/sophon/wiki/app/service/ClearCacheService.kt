package io.github.sophon.wiki.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.wiki.inPort.ClearCacheUseCase

internal class ClearCacheService : ClearCacheUseCase {
    override suspend fun invoke(): EmptyResult<WikiError> {
        TODO("Not yet implemented")
    }
}
