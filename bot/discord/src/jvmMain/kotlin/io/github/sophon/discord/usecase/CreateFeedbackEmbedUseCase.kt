package io.github.sophon.discord.usecase

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.domain.Result
import io.github.sophon.core.util.rollChance
import io.github.sophon.discord.BotError
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.domain.BotOutput

internal class CreateFeedbackEmbedUseCase {

    suspend fun MessageCreateEvent.invoke(
        feedback: BotOutput.Feedback,
    ): Result<Message, BotError> {
        return try {
            feedback.feedbackChannelList.forEach { channelId ->
                val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))

                channel?.createMessage {
                    embed(feedback.embedBuilder)
                }
            }
            val message = message.channel.createMessage {
                content = createResponseMessage()
            }

            Result.Success(message)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.invoke(
        feedback: BotOutput.Feedback,
    ): Result<PublicInteractionResponseBehavior, BotError> {
        return try {
            feedback.feedbackChannelList.forEach { channelId ->
                val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))

                channel?.createMessage {
                    embed(feedback.embedBuilder)
                }
            }
            val behavior = interaction.respondPublic {
                content = createResponseMessage()
            }

            Result.Success(behavior)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }


    private fun createResponseMessage(): String {
        return if (rollChance(successPercentage = 20)) {
            "Feedback sent successfully!\n" +
                    "Consider donating (`/donate` or `/tip`): **<$URL_KOFI>**"
        } else {
            "Feedback sent successfully!"
        }
    }
}