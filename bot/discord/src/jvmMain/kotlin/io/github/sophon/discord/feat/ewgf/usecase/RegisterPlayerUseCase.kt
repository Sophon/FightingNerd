package io.github.sophon.discord.feat.ewgf.usecase

import io.github.sophon.integration.EwgfClient
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.integration.model.Player

internal class RegisterPlayerUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(discordId: String, polarisId: String): EmptyResult<BotError> {
        val player = Player(polarisId = polarisId, discordId =  discordId)
        return client.registerPlayer(player)
            .mapError { it.toDomainError() }
    }
}