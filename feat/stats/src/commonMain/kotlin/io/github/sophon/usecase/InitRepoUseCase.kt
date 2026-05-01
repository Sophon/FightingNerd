package io.github.sophon.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.integration.data.ReportRepo
import io.github.sophon.integration.model.StatsError

@ExcludeFromCoverage("TODO: cover with test")
internal class InitRepoUseCase(
    private val repo: ReportRepo,
) {
    suspend fun invoke(): EmptyResult<StatsError> {
        return repo.init()
    }
}