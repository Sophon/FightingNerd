package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput

internal class CreateReplyEmbedUseCase {

    suspend fun invoke(
        message: Message,
        reply: BotOutput.Reply,
    ): Result<Message, BotError> {
        return try {
            val channel = message.kord.getChannelOf<MessageChannel>(Snowflake(reply.target.channelId))
            channel?.createMessage {
                content = "<@${reply.target.id}>"
                embed(reply.embedBuilder)
            }
            val sentMessage = message.channel.createMessage {
                content = if (channel == null) "Failed to send" else "Reply sent successfully!"
            }

            Result.Success(sentMessage)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun invoke(
        interaction: GuildChatInputCommandInteraction,
        reply: BotOutput.Reply,
    ): Result<PublicInteractionResponseBehavior, BotError> {
        return try {
            val channel = interaction.kord.getChannelOf<MessageChannel>(Snowflake(reply.target.channelId))
            channel?.createMessage {
                content = "<@${reply.target.id}>"
                embed(reply.embedBuilder)
            }
            val behavior = interaction.respondPublic {
                content = if (channel == null) "Failed to send" else "Reply sent successfully!"
            }

            Result.Success(behavior)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }
}
