package io.github.sophon.discord.feat.ewgf.usecase

import io.github.sophon.EwgfClient
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.domain.model.Player

internal class UpdatePlayerUseCase(
    private val client: EwgfClient,
) {
    suspend fun invoke(player: Player): EmptyResult<BotError> {
        return client.updatePolarisId(player)
            .mapError { it.toDomainError() }
    }
}