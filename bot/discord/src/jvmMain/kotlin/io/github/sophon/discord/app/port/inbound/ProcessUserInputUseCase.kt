package io.github.sophon.discord.app.port.inbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.DiscordCommandInteraction
import io.github.sophon.discord.app.domain.model.Message
import io.github.sophon.discord.feat.core.domain.model.BotError

internal interface ProcessUserInputUseCase {
    suspend operator fun invoke(
        message: Message,
        botId: String,
    ): Result<BotResponse, BotError>

    suspend operator fun invoke(
        discordCommandInteraction: DiscordCommandInteraction,
    ): Result<BotResponse, BotError>
}
