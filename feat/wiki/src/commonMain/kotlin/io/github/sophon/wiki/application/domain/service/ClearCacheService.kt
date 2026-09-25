package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.wiki.application.port.inbound.ClearCacheUseCase

internal class ClearCacheService : ClearCacheUseCase {
    override suspend fun invoke(): EmptyResult<WikiError> {
        TODO("Not yet implemented")
    }
}
