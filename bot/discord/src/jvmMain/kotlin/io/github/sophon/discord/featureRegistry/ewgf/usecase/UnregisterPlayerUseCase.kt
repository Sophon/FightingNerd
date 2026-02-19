package io.github.sophon.discord.featureRegistry.ewgf.usecase

import io.github.sophon.EwgfClient
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError

internal class UnregisterPlayerUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(discordId: String): EmptyResult<BotError> {
        return client.deletePlayer(discordId)
            .mapError { it.toDomainError() }
    }
}