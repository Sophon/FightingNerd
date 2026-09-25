package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.wiki.data.WikiError

interface ClearCacheUseCase {
    suspend operator fun invoke(): EmptyResult<WikiError>
}
