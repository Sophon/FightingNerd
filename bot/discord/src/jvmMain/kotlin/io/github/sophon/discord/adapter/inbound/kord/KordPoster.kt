package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.common.Color
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.FollowupPermittingInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.createPublicFollowup
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.util.rollChance
import io.github.sophon.discord.RNG_DONATION_PCT_COMMAND
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.feat.bot.usecase.CreatePromoEmbedUseCase
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@ExcludeFromCoverage("UI")
internal class KordPoster(
    private val createPromoEmbedUseCase: CreatePromoEmbedUseCase,
    private val discordButtonBuilder: DiscordButtonBuilder,
) {
    suspend fun post(
        message: Message,
        embedBuilder: EmbedBuilder.() -> Unit,
        imageList: List<String>,
        isExpanded: Boolean,
        buttonSet: BotResponse.ButtonSet?,
    ): EmptyResult<BotError> {
        val result = try {
            message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                moveContent(
                    embedBuilder = embedBuilder,
                    imageList = imageList,
                    isExpanded = isExpanded,
                    buttonSet = buttonSet,
                )
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            if (e.status.code == HTTP_FORBIDDEN) {
                message.channel.createMessage {
                    embed(missingPermissionsEmbed(errorMessage = e.message))
                }
            }

            Result.Error(BotError.Kord(e.toString()))
        }

        result.onSuccess { rollForPromo(message.channel) }

        return result
    }

    suspend fun post(
        interaction: GuildChatInputCommandInteraction,
        embedBuilder: EmbedBuilder.() -> Unit,
        imageList: List<String>,
        isExpanded: Boolean,
        buttonSet: BotResponse.ButtonSet?,
    ): EmptyResult<BotError> {
        val result = try {
            interaction.respondPublic {
                moveContent(
                    embedBuilder = embedBuilder,
                    imageList = imageList,
                    isExpanded = isExpanded,
                    buttonSet = buttonSet,
                )
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            if (e.status.code == HTTP_FORBIDDEN) {
                interaction.respondPublic {
                    embed(missingPermissionsEmbed(errorMessage = e.message))
                }
            }

            Result.Error(BotError.Kord(e.toString()))
        }

        result.onSuccess { rollForPromo(interaction.channel) }

        return result
    }

    /**
     * Replaces the message's embeds and buttons in place.
     */
    suspend fun edit(
        message: Message,
        embedBuilder: EmbedBuilder.() -> Unit,
        imageList: List<String>,
        isExpanded: Boolean,
        buttonSet: BotResponse.ButtonSet?,
    ): EmptyResult<BotError> {
        val result = try {
            message.edit {
                embeds = mutableListOf()
                components = mutableListOf()
                moveContent(
                    embedBuilder = embedBuilder,
                    imageList = imageList,
                    isExpanded = isExpanded,
                    buttonSet = buttonSet,
                )
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    suspend fun postText(
        response: FollowupPermittingInteractionResponseBehavior,
        mention: String,
        text: String,
    ): EmptyResult<BotError> {
        val result = try {
            response.createPublicFollowup {
                content = buildString {
                    appendLine(mention)
                    append(text)
                }
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }


    /**
     * Image embeds share the primary embed's title and url, so Discord groups them under the same link.
     */
    private fun MessageBuilder.moveContent(
        embedBuilder: EmbedBuilder.() -> Unit,
        imageList: List<String>,
        isExpanded: Boolean,
        buttonSet: BotResponse.ButtonSet?,
    ) {
        embed(embedBuilder)

        val primaryEmbed = EmbedBuilder().apply(embedBuilder)

        if (isExpanded && imageList.size >= 2) {
            imageList.forEach { imageUrl ->
                embed {
                    title = primaryEmbed.title
                    url = primaryEmbed.url
                    image = imageUrl
                }
            }
        }

        buttonSet?.let { discordButtonBuilder.createResponseButtons(messageBuilder = this, buttonSet = it) }
    }

    private fun missingPermissionsEmbed(errorMessage: String?): EmbedBuilder.() -> Unit = {
        title = "⚠️ Error"
        color = Color(YELLOW)
        mandatoryField(
            name = "",
            value = errorMessage,
        )
    }

    private suspend fun rollForPromo(channel: MessageChannelBehavior) {
        if (rollChance(RNG_DONATION_PCT_COMMAND)) {
            createPromoEmbedUseCase.invoke(channel)
        }
    }


    private companion object {
        const val HTTP_FORBIDDEN = 403
        const val YELLOW = 0x00FFC107
    }
}
