package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.FollowupPermittingInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.createPublicFollowup
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
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
import io.github.sophon.discord.adapter.inbound.kord.ui.aliasEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.aliasGamePromptEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.banEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.commandsEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.errorEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.ewgfHelpEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.feedbackEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.glossaryEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.helpEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.imagesMediaEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.mandatoryField
import io.github.sophon.discord.adapter.inbound.kord.ui.modulesEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.promoButtonSet
import io.github.sophon.discord.adapter.inbound.kord.ui.promoEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.recentSetsEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.replyEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.steamLobbyEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.successEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.tipEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.unbanEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.videoMediaText
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.CoreResponse
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.discord.app.model.response.FeedbackResponse
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.discord.app.model.response.MediaResponse
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.ReplyResponse
import io.github.sophon.discord.app.model.response.SteamLobbyResponse
import io.github.sophon.discord.app.model.response.UnbanResponse
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@ExcludeFromCoverage("UI")
internal class KordResponder(
    private val discordButtonBuilder: DiscordButtonBuilder,
    private val commandRegistry: CommandRegistry,
) {
    suspend fun respond(
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

    suspend fun respond(
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

    suspend fun respond(
        message: Message,
        coreResponse: CoreResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = coreEmbed(coreResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = coreResponse.buttonSet,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        coreResponse: CoreResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = coreEmbed(coreResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = coreResponse.buttonSet,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        modulesResponse: ModulesResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = modulesEmbed(modulesResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        modulesResponse: ModulesResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = modulesEmbed(modulesResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        steamLobby: SteamLobbyResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = steamLobbyEmbed(steamLobby),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = steamLobby.buttonSet,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        steamLobby: SteamLobbyResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = steamLobbyEmbed(steamLobby),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = steamLobby.buttonSet,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        aliasResponse: AliasResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = aliasResponseEmbed(aliasResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = (aliasResponse as? AliasResponse.GamePrompt)?.buttonSet,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        aliasResponse: AliasResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = aliasResponseEmbed(aliasResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = (aliasResponse as? AliasResponse.GamePrompt)?.buttonSet,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        ewgfResponse: EwgfResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = ewgfResponseEmbed(ewgfResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        ewgfResponse: EwgfResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = ewgfResponseEmbed(ewgfResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        glossaryResponse: GlossaryResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = glossaryEmbed(glossaryResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        glossaryResponse: GlossaryResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = glossaryEmbed(glossaryResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        mediaResponse: MediaResponse,
    ): EmptyResult<BotError> {
        val result = when (mediaResponse) {
            is MediaResponse.ImagesMediaResponse -> respond(
                message = message,
                embedBuilder = imagesMediaEmbed(mediaResponse),
                imageList = mediaResponse.imageList,
                isExpanded = true,
                buttonSet = null,
            )

            is MediaResponse.VideoMediaResponse -> respond(
                message = message,
                plainText = PlainTextResponse(text = videoMediaText(mediaResponse)),
            )

            MediaResponse.NoMedia -> respond(
                message = message,
                plainText = PlainTextResponse(text = NO_MEDIA),
            )
        }
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        mediaResponse: MediaResponse,
    ): EmptyResult<BotError> {
        val result = when (mediaResponse) {
            is MediaResponse.ImagesMediaResponse -> respond(
                interaction = interaction,
                embedBuilder = imagesMediaEmbed(mediaResponse),
                imageList = mediaResponse.imageList,
                isExpanded = true,
                buttonSet = null,
            )

            is MediaResponse.VideoMediaResponse -> respond(
                interaction = interaction,
                plainText = PlainTextResponse(text = videoMediaText(mediaResponse)),
            )

            MediaResponse.NoMedia -> respond(
                interaction = interaction,
                plainText = PlainTextResponse(text = NO_MEDIA),
            )
        }
        return result
    }

    /**
     * Posts the feedback to every feedback channel, then confirms to the author.
     */
    suspend fun respond(
        message: Message,
        feedback: FeedbackResponse,
    ): EmptyResult<BotError> {
        val result = try {
            postFeedback(kord = message.kord, feedback = feedback)
            message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                content = FEEDBACK_SENT
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    /**
     * Posts the feedback to every feedback channel, then confirms to the author.
     */
    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        feedback: FeedbackResponse,
    ): EmptyResult<BotError> {
        val result = try {
            postFeedback(kord = interaction.kord, feedback = feedback)
            interaction.respondPublic {
                content = FEEDBACK_SENT
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    suspend fun respond(
        message: Message,
        reply: ReplyResponse,
    ): EmptyResult<BotError> {
        val result = sendToRecipient(
            message = message,
            recipient = reply.recipient,
            embedBuilder = replyEmbed(reply),
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        reply: ReplyResponse,
    ): EmptyResult<BotError> {
        val result = sendToRecipient(
            interaction = interaction,
            recipient = reply.recipient,
            embedBuilder = replyEmbed(reply),
        )
        return result
    }

    suspend fun respond(
        message: Message,
        ban: BanResponse,
    ): EmptyResult<BotError> {
        val result = sendToRecipient(
            message = message,
            recipient = ban.offender,
            embedBuilder = banEmbed(ban),
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        ban: BanResponse,
    ): EmptyResult<BotError> {
        val result = sendToRecipient(
            interaction = interaction,
            recipient = ban.offender,
            embedBuilder = banEmbed(ban),
        )
        return result
    }

    suspend fun respond(
        message: Message,
        unban: UnbanResponse,
    ): EmptyResult<BotError> {
        val result = sendToRecipient(
            message = message,
            recipient = unban.offender,
            embedBuilder = unbanEmbed(unban),
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        unban: UnbanResponse,
    ): EmptyResult<BotError> {
        val result = sendToRecipient(
            interaction = interaction,
            recipient = unban.offender,
            embedBuilder = unbanEmbed(unban),
        )
        return result
    }

    suspend fun respond(
        message: Message,
        plainText: PlainTextResponse,
    ): EmptyResult<BotError> {
        val result = try {
            message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                textContent(plainText)
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        plainText: PlainTextResponse,
    ): EmptyResult<BotError> {
        val result = try {
            interaction.respondPublic {
                textContent(plainText)
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    suspend fun respond(
        message: Message,
        botError: BotError,
    ): EmptyResult<BotError> {
        val result = try {
            message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                embed(errorEmbed(error = botError, commandRegistry = commandRegistry))
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        botError: BotError,
    ): EmptyResult<BotError> {
        val result = try {
            interaction.respondPublic {
                embed(errorEmbed(error = botError, commandRegistry = commandRegistry))
            }

            Result.Success(Unit)
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

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

    suspend fun respondText(
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

    private suspend fun postFeedback(
        kord: Kord,
        feedback: FeedbackResponse,
    ) {
        val embedBuilder = feedbackEmbed(feedback)
        feedback.feedbackChannelIdList.forEach { channelId ->
            kord.getChannelOf<TextChannel>(Snowflake(channelId))
                ?.createMessage {
                    embed(embedBuilder)
                    discordButtonBuilder.createResponseButtons(messageBuilder = this, buttonSet = feedback.buttonSet)
                }
        }
    }

    /**
     * Posts the embed to the recipient's channel, then confirms to the admin.
     */
    private suspend fun sendToRecipient(
        message: Message,
        recipient: UserRequest.Source,
        embedBuilder: EmbedBuilder.() -> Unit,
    ): EmptyResult<BotError> {
        val result = try {
            val postResult = postToRecipient(kord = message.kord, recipient = recipient, embedBuilder = embedBuilder)
            message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }
                content = if (postResult is Result.Success) REPLY_SENT else REPLY_FAILED
            }

            postResult
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    /**
     * Posts the embed to the recipient's channel, then confirms to the admin.
     */
    private suspend fun sendToRecipient(
        interaction: GuildChatInputCommandInteraction,
        recipient: UserRequest.Source,
        embedBuilder: EmbedBuilder.() -> Unit,
    ): EmptyResult<BotError> {
        val result = try {
            val postResult = postToRecipient(kord = interaction.kord, recipient = recipient, embedBuilder = embedBuilder)
            interaction.respondPublic {
                content = if (postResult is Result.Success) REPLY_SENT else REPLY_FAILED
            }

            postResult
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }

        return result
    }

    private suspend fun postToRecipient(
        kord: Kord,
        recipient: UserRequest.Source,
        embedBuilder: EmbedBuilder.() -> Unit,
    ): EmptyResult<BotError> {
        val channel = kord.getChannelOf<MessageChannel>(Snowflake(recipient.channelId))

        val result = if (channel == null) {
            Result.Error(BotError.Kord("Channel not found: ${recipient.channelId}"))
        } else {
            channel.createMessage {
                content = "<@${recipient.id}>"
                embed(embedBuilder)
            }
            Result.Success(Unit)
        }
        return result
    }

    private fun MessageBuilder.textContent(plainText: PlainTextResponse) {
        content = plainText.text
        plainText.buttonSet?.let { discordButtonBuilder.createResponseButtons(messageBuilder = this, buttonSet = it) }
    }

    private fun coreEmbed(coreResponse: CoreResponse): EmbedBuilder.() -> Unit {
        val dataSource = coreResponse.dataSource
        val embedBuilder = when (coreResponse.type) {
            CoreResponse.Type.Tip -> tipEmbed(dataSource)
            CoreResponse.Type.Help -> helpEmbed(commandRegistry, dataSource)
            CoreResponse.Type.Commands -> commandsEmbed(
                commandList = Command.entries.sortedBy { it.name },
                commandRegistry = commandRegistry,
                dataSource = dataSource,
            )
        }
        return embedBuilder
    }

    private fun aliasResponseEmbed(aliasResponse: AliasResponse): EmbedBuilder.() -> Unit {
        val embedBuilder = when (aliasResponse) {
            is AliasResponse.CharacterAliases -> aliasEmbed(aliasResponse.characterList)
            is AliasResponse.GamePrompt -> aliasGamePromptEmbed(aliasResponse)
        }
        return embedBuilder
    }

    private fun ewgfResponseEmbed(ewgfResponse: EwgfResponse): EmbedBuilder.() -> Unit {
        val embedBuilder = when (ewgfResponse) {
            is EwgfResponse.RecentSets -> recentSetsEmbed(ewgfResponse)
            is EwgfResponse.Success -> successEmbed(ewgfResponse)
            is EwgfResponse.Help -> ewgfHelpEmbed(ewgfResponse)
        }
        return embedBuilder
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
            channel.createMessage {
                embed(promoEmbed())
                discordButtonBuilder.createResponseButtons(messageBuilder = this, buttonSet = promoButtonSet())
            }
        }
    }


    private companion object {
        const val HTTP_FORBIDDEN = 403
        const val YELLOW = 0x00FFC107
        const val FEEDBACK_SENT = "Feedback sent successfully!"
        const val REPLY_SENT = "Reply sent successfully!"
        const val REPLY_FAILED = "Failed to send"
        const val NO_MEDIA = "No media found 😔"
    }
}
