package io.github.sophon.discord.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.BotResponse
import io.github.sophon.discord.app.model.ButtonEvent

internal interface ProcessButtonEventUseCase {
    suspend operator fun invoke(buttonEvent: ButtonEvent): Result<BotResponse, BotError>
}
