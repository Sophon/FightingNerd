package io.github.sophon.discord.app.port.inbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.ButtonEvent
import io.github.sophon.discord.feat.core.domain.model.BotError

internal interface ProcessButtonEventUseCase {
    suspend operator fun invoke(buttonEvent: ButtonEvent): Result<BotResponse, BotError>
}
