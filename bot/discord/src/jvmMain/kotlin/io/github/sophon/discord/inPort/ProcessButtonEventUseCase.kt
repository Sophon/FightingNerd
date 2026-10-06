package io.github.sophon.discord.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.ButtonEvent
import io.github.sophon.discord.app.model.response.BotResponse

internal interface ProcessButtonEventUseCase {
    suspend operator fun invoke(buttonEvent: ButtonEvent): Result<BotResponse, BotError>
}
