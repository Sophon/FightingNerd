package io.github.sophon.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.integration.data.PlayerRepo
import io.github.sophon.data.remote.EwgfDataSource
import io.github.sophon.data.toDomain
import io.github.sophon.integration.model.Battle
import io.github.sophon.integration.model.EwgfError

@ExcludeFromCoverage("to be implemented")
internal class DownloadPlayerBattlesUseCase(
    private val repo: PlayerRepo,
    private val source: EwgfDataSource,
) {
    suspend fun invoke(discordId: String): Result<List<Battle>, EwgfError> {
        val player = when (val result = repo.getPlayer(discordId)) {
            is Result.Success -> {
                result.data ?: return Result.Error(EwgfError.PlayerNotRegistered(discordId))
            }
            is Result.Error -> {
                return Result.Error(EwgfError.PlayerNotFound(discordId))
            }
        }

        val result = source.getBattles(player.polarisId)
            .map { dto -> dto.toDomain(player.polarisId) }
            .mapError { EwgfError.PlayerNotFound(player.polarisId) }
        return result
    }
}
