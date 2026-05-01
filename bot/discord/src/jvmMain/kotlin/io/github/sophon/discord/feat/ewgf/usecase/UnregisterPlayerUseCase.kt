package io.github.sophon.discord.feat.ewgf.usecase

import io.github.sophon.integration.EwgfClient
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class UnregisterPlayerUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(discordId: String): EmptyResult<BotError> {
        return client.deletePlayer(discordId)
            .mapError { it.toDomainError() }
    }
}