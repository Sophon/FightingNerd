package io.github.sophon.discord.feat.ewgf.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.integration.EwgfClient
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.integration.model.BattleSet

@ExcludeFromCoverage("plain client call")
internal class GetRecentMatchesUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(discordId: String): Result<List<BattleSet>, BotError> {
        return client.downloadBattleData(discordId)
            .mapError { it.toDomainError() }
    }
}