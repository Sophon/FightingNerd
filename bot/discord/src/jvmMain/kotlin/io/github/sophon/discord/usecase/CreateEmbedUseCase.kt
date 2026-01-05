package io.github.sophon.discord.usecase

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.domain.Result
import io.github.sophon.core.util.rollChance
import io.github.sophon.discord.BotError
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.domain.BotOutput

/**
 * USED FOR: general and error embeds
 */
internal class CreateEmbedUseCase {

    suspend fun MessageCreateEvent.invoke(
        embedBuilder: EmbedBuilder.() -> Unit,
        imageList: BotOutput.Images? = null,
        buttons: List<BotOutput.EmbedButton> = listOf(),
    ): Result<Message, BotError> {
        return try {
            val message = message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                embed(embedBuilder)

                if (buttons.isNotEmpty()) {
                    createButtons(buttons)
                }

                imageList?.urls?.forEach { url ->
                    embed {
                        title = imageList.title
                        this.url = imageList.titleUrl
                        image = url
                    }
                }
            }

            if (rollChance(successPercentage = 1)) {
                message.channel.createMessage {
                    content = "Consider donating (`/donate` or `/tip`): **<$URL_KOFI>**"
                }
            }

            Result.Success(message)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.invoke(
        embedBuilder: EmbedBuilder.() -> Unit,
        imageList: BotOutput.Images? = null,
        buttons: List<BotOutput.EmbedButton> = listOf(),
    ): Result<PublicInteractionResponseBehavior, BotError> {
        return try {
            val behavior = interaction.respondPublic {
                embed(embedBuilder)

                if (buttons.isNotEmpty()) {
                    createButtons(buttons)
                }

                imageList?.urls?.forEach { url ->
                    embed {
                        title = imageList.title
                        this.url = imageList.titleUrl
                        image = url
                    }
                }
            }

            if (rollChance(successPercentage = 1)) {
                interaction.channel.createMessage {
                    content = "Consider donating (`/donate` or `/tip`): **<$URL_KOFI>**"
                }
            }

            Result.Success(behavior)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }


    private fun MessageBuilder.createButtons(buttons: List<BotOutput.EmbedButton>) {
        buttons.chunked(5).forEach { rowButtons ->
            actionRow {
                rowButtons.forEach { button ->
                    interactionButton(
                        style = ButtonStyle.Secondary,
                        customId = button.label,
                    ) {
                        label = button.label
                        disabled = false
                    }
                }
            }
        }
    }
}