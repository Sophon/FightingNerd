package io.github.sophon.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.data.PlayerRepo
import io.github.sophon.domain.EwgfError
import io.github.sophon.domain.Player

@ExcludeFromCoverage("to be implemented")
internal class RegisterPlayerUseCase(
    private val repo: PlayerRepo,
) {
    suspend fun invoke(player: Player): EmptyResult<EwgfError> {
        return repo.registerPlayer(player)
    }
}