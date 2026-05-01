package io.github.sophon.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.data.PlayerRepo
import io.github.sophon.integration.model.EwgfError
import io.github.sophon.integration.model.Player

@ExcludeFromCoverage("to be implemented")
internal class RegisterPlayerUseCase(
    private val repo: PlayerRepo,
) {
    suspend fun invoke(player: Player): EmptyResult<EwgfError> {
        val formatted = player.copy(
            polarisId = player.polarisId.replace("-", "")
        )
        return repo.registerPlayer(formatted)
    }
}
