package io.github.sophon.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.integration.model.Command
import io.github.sophon.integration.model.StatsError

@ExcludeFromCoverage("TODO: cover with test")
internal class ResetCacheUseCase {
    fun invoke(cache: MutableMap<Command, Long>): EmptyResult<StatsError> {
        cache.clear()
        return Result.Success(Unit)
    }
}
