package io.github.sophon.discord.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.model.BotError

interface StartFeaturesUseCase {
    suspend operator fun invoke(): EmptyResult<BotError>
}
