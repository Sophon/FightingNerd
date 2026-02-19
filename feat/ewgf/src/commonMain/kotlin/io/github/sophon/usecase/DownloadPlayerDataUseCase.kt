package io.github.sophon.usecase

import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.core.domain.Result
import io.github.sophon.data.PlayerRepo
import io.github.sophon.domain.EwgfError
import io.github.sophon.domain.Player

@ExcludeFromCoverage("to be implemented")
internal class DownloadPlayerDataUseCase(
    private val repo: PlayerRepo,
) {
    suspend fun invoke(discordId: String): Result<Player, EwgfError> {
        return when (val result = repo.getPlayer(discordId)) {
            is Result.Success -> {
                if (result.data == null) {
                    Result.Error(EwgfError.PlayerNotFound(discordId))
                } else {
                    Result.Success(result.data!!)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }
}