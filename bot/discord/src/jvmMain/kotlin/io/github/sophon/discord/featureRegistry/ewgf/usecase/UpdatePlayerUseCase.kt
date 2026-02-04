package io.github.sophon.discord.featureRegistry.ewgf.usecase

import io.github.sophon.EwgfClient
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.Player

internal class UpdatePlayerUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(player: Player): EmptyResult<BotError> {
        return client.updatePolarisId(player)
            .mapError { it.toDomainError() }
    }
}