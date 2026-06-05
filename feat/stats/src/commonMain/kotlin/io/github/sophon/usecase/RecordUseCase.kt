package io.github.sophon.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
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
