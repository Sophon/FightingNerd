package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import kotlinx.datetime.Instant

class GetLastCacheInsertInstantUseCase(
    private val get: suspend () -> Result<Instant?, WikiError>
) {
    suspend fun invoke(): Result<Instant?, WikiError> {
        return get()
    }
}