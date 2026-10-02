package io.github.sophon.discord.app.port.inbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.domain.model.BotError

interface StartFeaturesUseCase {
    suspend operator fun invoke(): EmptyResult<BotError>
}
