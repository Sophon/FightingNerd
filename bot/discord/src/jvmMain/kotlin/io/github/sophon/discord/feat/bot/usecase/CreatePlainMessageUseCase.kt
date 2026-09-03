package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.integration.model.Source

@ExcludeFromCoverage("UI")
internal class CreatePlainMessageUseCase {
    suspend operator fun invoke(
        message: Message,
        text: String,
        source: Source,
    ): Result<Message, BotError> {
        return try {
            val sentMessage = message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                content = text
            }
            Result.Success(sentMessage)
        }  catch (e: RestRequestException) {
            Result.Error(BotError.Kord("${source.serverName}: ${e.toString()}"))
        }
    }

    suspend operator fun invoke(
        interaction: GuildChatInputCommandInteraction,
        text: String,
        source: Source,
    ): Result<PublicInteractionResponseBehavior, BotError> {
        return try {
            val behavior = interaction.respondPublic { content = text }
            Result.Success(behavior)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord("${source.serverName}: ${e.toString()}"))
        }
    }
}
