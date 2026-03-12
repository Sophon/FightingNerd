package io.github.sophon.discord.featureRegistry.ewgf.usecase

import io.github.sophon.EwgfClient
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.model.Player

internal class RegisterPlayerUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(discordId: String, polarisId: String): EmptyResult<BotError> {
        val player = Player(polarisId = polarisId, discordId =  discordId)
        return client.registerPlayer(player)
            .mapError { it.toDomainError() }
    }
}