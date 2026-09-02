package io.github.sophon.discord.feat.ewgf.usecase

import io.github.sophon.integration.EwgfClient
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

@ExcludeFromCoverage("plain client call")
internal class UnregisterPlayerUseCase(
    private val client: EwgfClient,
) {
    suspend operator fun invoke(discordId: String): EmptyResult<BotError> {
        return client.deletePlayer(discordId)
            .mapError { it.toDomainError() }
    }
}