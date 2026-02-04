package io.github.sophon.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.data.PlayerRepo
import io.github.sophon.domain.EwgfError

internal class UpdatePolarisIdUseCase(
    private val repo: PlayerRepo,
) {
    suspend fun invoke(
        discordId: String,
        polarisId: String,
    ): EmptyResult<EwgfError> {
        return repo.updatePolarisId(discordId = discordId, polarisId = polarisId)
    }
}