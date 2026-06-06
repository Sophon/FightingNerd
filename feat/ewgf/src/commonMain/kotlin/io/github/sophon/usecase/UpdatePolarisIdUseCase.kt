package io.github.sophon.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.integration.data.PlayerRepo
import io.github.sophon.integration.model.EwgfError

@ExcludeFromCoverage("to be implemented")
internal class UpdatePolarisIdUseCase(
    private val repo: PlayerRepo,
) {
    suspend fun invoke(
        discordId: String,
        polarisId: String,
    ): EmptyResult<EwgfError> {
        return repo.updatePolarisId(discordId = discordId, polarisId = polarisId.replace("-", ""))
    }
}