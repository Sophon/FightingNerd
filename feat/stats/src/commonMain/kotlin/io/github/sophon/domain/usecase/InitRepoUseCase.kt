package io.github.sophon.domain.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.data.ReportRepo
import io.github.sophon.domain.StatsError

@ExcludeFromCoverage("TODO: cover with test")
internal class InitRepoUseCase(
    private val repo: ReportRepo,
) {
    suspend fun invoke(): EmptyResult<StatsError> {
        return repo.init()
    }
}