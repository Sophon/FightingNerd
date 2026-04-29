package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.domain.model.BotError

internal class CreatePlainMessageUseCase {

    suspend fun MessageCreateEvent.invoke(text: String): Result<Message, BotError> {
        return try {
            val message = message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                content = text
            }
            Result.Success(message)
        }  catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.invoke(
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