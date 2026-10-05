package io.github.sophon.discord.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.BotResponse
import io.github.sophon.discord.app.model.DiscordCommandInteraction
import io.github.sophon.discord.app.model.Message

internal interface ProcessUserInputUseCase {
    suspend operator fun invoke(
        message: Message,
        botId: String,
    ): Result<BotResponse, BotError>

    suspend operator fun invoke(
        discordCommandInteraction: DiscordCommandInteraction,
    ): Result<BotResponse, BotError>
}
