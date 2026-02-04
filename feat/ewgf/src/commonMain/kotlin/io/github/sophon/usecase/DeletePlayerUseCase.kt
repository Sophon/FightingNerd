package io.github.sophon.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.data.PlayerRepo
import io.github.sophon.domain.EwgfError

internal class DeletePlayerUseCase(
    private val repo: PlayerRepo,
) {
    suspend fun invoke(discordId: String): EmptyResult<EwgfError> {
        return repo.deletePlayer(discordId)
    }
}