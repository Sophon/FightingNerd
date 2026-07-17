package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.feat.core.domain.model.BotError

internal class CreatePlainMessageUseCase {

    suspend fun invoke(
        message: Message,
        text: String,
    ): Result<Message, BotError> {
        return try {
            val sentMessage = message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                content = text
            }
            Result.Success(sentMessage)
        }  catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun invoke(
        interaction: GuildChatInputCommandInteraction,
        text: String,
    ): Result<PublicInteractionResponseBehavior, BotError> {
        return try {
            val behavior = interaction.respondPublic { content = text }
            Result.Success(behavior)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }
}
