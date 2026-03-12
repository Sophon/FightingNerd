package io.github.sophon.discord.featureRegistry.ewgf.usecase

import io.github.sophon.EwgfClient
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.model.BattleSet

internal class GetRecentMatchesUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(discordId: String): Result<List<BattleSet>, BotError> {
        return client.downloadBattleData(discordId)
            .mapError { it.toDomainError() }
    }
}