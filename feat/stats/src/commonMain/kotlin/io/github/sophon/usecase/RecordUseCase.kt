package io.github.sophon.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.core.domain.Result
import io.github.sophon.integration.model.Command
import io.github.sophon.integration.model.StatsError

@ExcludeFromCoverage("TODO: cover with test")
internal class RecordUseCase() {
    fun invoke(
        command: Command,
        cache: MutableMap<Command, Long>,
    ): EmptyResult<StatsError> {
        cache[command] = cache.getOrElse(key = command, defaultValue = { 0L }) + 1
        return Result.Success(Unit)
    }
}
