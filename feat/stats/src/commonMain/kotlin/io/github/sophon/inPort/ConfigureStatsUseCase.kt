package io.github.sophon.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.StatsError

interface ConfigureStatsUseCase {
    suspend operator fun invoke(): EmptyResult<StatsError>
}
