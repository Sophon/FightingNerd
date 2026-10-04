package io.github.sophon.discord.app.port.inbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.ButtonEvent

internal interface ProcessButtonEventUseCase {
    suspend operator fun invoke(buttonEvent: ButtonEvent): Result<BotResponse, BotError>
}
