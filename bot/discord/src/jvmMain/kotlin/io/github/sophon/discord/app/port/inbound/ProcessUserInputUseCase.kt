package io.github.sophon.discord.app.port.inbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.DiscordCommandInteraction
import io.github.sophon.discord.app.domain.model.Message
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput

internal interface ProcessUserInputUseCase {
    suspend operator fun invoke(
        message: Message,
        botId: String,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ): Result<BotResponse, BotError>

    suspend operator fun invoke(
        discordCommandInteraction: DiscordCommandInteraction,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ): Result<BotResponse, BotError>
}
