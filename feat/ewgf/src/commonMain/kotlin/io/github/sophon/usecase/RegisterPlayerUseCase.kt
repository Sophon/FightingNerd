package io.github.sophon.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.integration.data.PlayerRepo
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
