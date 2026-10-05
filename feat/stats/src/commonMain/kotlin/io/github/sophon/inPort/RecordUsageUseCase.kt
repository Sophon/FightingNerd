package io.github.sophon.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.Command
import io.github.sophon.model.StatsError

interface RecordUsageUseCase {
    suspend operator fun invoke(command: Command): EmptyResult<StatsError>
}
