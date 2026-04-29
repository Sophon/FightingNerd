package io.github.sophon.discord.feat.bot.usecase

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
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.domain.DiscordButtonBuilder
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.model.BotOutput
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal class CreateFeedbackEmbedUseCase(
    private val discordButtonBuilder: DiscordButtonBuilder,
) {

    suspend fun MessageCreateEvent.invoke(
        feedback: BotOutput.Feedback,
        buttonSet: BotOutput.ButtonSet?,
    ): Result<Message, BotError> {
        return try {
            feedback.feedbackChannelList.forEach { channelId ->
                val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))

                channel?.createMessage {
                    embed(feedback.embedBuilder)
                    if (buttonSet?.buttonList.isNullOrEmpty().not()) {
                        discordButtonBuilder.createEmbedButtons(
                            messageBuilder = this,
                            buttonList = buttonSet.buttonList,
                        )
                    }
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
        buttonSet: BotOutput.ButtonSet?,
    ): Result<PublicInteractionResponseBehavior, BotError> {
        return try {
            feedback.feedbackChannelList.forEach { channelId ->
                val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))

                channel?.createMessage {
                    embed(feedback.embedBuilder)
                    if (buttonSet?.buttonList.isNullOrEmpty().not()) {
                        discordButtonBuilder.createEmbedButtons(
                            messageBuilder = this,
                            buttonList = buttonSet.buttonList,
                        )
                    }
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
                    "Consider donating (`/donate` or `/tip`): **<${URL_KOFI}>**"
        } else {
            "Feedback sent successfully!"
        }
    }
}