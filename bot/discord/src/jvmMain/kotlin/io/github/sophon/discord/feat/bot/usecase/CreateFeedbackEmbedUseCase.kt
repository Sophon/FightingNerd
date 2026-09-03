package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.feat.core.domain.DiscordButtonBuilder
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.integration.model.Source
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@ExcludeFromCoverage("UI")
internal class CreateFeedbackEmbedUseCase(
    private val discordButtonBuilder: DiscordButtonBuilder,
) {

    suspend operator fun invoke(
        message: Message,
        feedback: BotOutput.Feedback,
        buttonSet: BotOutput.ButtonSet?,
        source: Source,
    ): Result<Message, BotError> {
        return try {
            feedback.feedbackChannelList.forEach { channelId ->
                val channel = message.kord.getChannelOf<TextChannel>(Snowflake(channelId))

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
            val sentMessage = message.channel.createMessage {
                content = "Feedback sent successfully!"
            }

            Result.Success(sentMessage)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord("${source.serverName}: ${e.toString()}"))
        }
    }

    suspend operator fun invoke(
        interaction: GuildChatInputCommandInteraction,
        feedback: BotOutput.Feedback,
        buttonSet: BotOutput.ButtonSet?,
        source: Source,
    ): Result<PublicInteractionResponseBehavior, BotError> {
        return try {
            feedback.feedbackChannelList.forEach { channelId ->
                val channel = interaction.kord.getChannelOf<TextChannel>(Snowflake(channelId))

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
                content = "Feedback sent successfully!"
            }

            Result.Success(behavior)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord("${source.serverName}: ${e.toString()}"))
        }
    }
}
