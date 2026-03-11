package io.github.sophon.domain.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.StatsError
import io.github.sophon.domain.model.Command

@ExcludeFromCoverage("TODO: cover with test")
internal class ResetCacheUseCase {
    fun invoke(cache: MutableMap<Command, Long>): EmptyResult<StatsError> {
        cache.clear()
        return Result.Success(Unit)
    }
}
