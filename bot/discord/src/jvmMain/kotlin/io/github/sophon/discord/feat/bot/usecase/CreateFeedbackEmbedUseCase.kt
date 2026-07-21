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
import io.github.sophon.core.util.rollChance
import io.github.sophon.discord.RNG_DONATION_PCT_FEEDBACK
import io.github.sophon.discord.feat.core.domain.DiscordButtonBuilder
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.util.donationMessage
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@ExcludeFromCoverage("UI")
internal class CreateFeedbackEmbedUseCase(
    private val discordButtonBuilder: DiscordButtonBuilder,
) {

    suspend fun invoke(
        message: Message,
        feedback: BotOutput.Feedback,
        buttonSet: BotOutput.ButtonSet?,
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
                content = createResponseMessage()
            }

            Result.Success(sentMessage)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun invoke(
        interaction: GuildChatInputCommandInteraction,
        feedback: BotOutput.Feedback,
        buttonSet: BotOutput.ButtonSet?,
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
                content = createResponseMessage()
            }

            Result.Success(behavior)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }


    private fun createResponseMessage(): String {
        return if (rollChance(successPercentage = RNG_DONATION_PCT_FEEDBACK)) {
            "Feedback sent successfully!\n" + donationMessage()
        } else {
            "Feedback sent successfully!"
        }
    }
}
