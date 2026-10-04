package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.common.Color
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.FollowupPermittingInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.createPublicFollowup
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
import io.github.sophon.discord.adapter.inbound.kord.ui.aliasEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.aliasGamePromptEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.commandsEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.errorEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.helpEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.mandatoryField
import io.github.sophon.discord.adapter.inbound.kord.ui.modulesEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.tipEmbed
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.feat.bot.usecase.CreatePromoEmbedUseCase
import io.github.sophon.discord.feat.core.domain.CommandRegistry
import io.github.sophon.discord.feat.core.usecase.GetBotFeatureInfoUseCase
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@ExcludeFromCoverage("UI")
internal class KordResponder(
    private val createPromoEmbedUseCase: CreatePromoEmbedUseCase,
    private val discordButtonBuilder: DiscordButtonBuilder,
    private val commandRegistry: CommandRegistry,
    private val getBotFeatureInfoUseCase: GetBotFeatureInfoUseCase,
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
        coreResponse: BotResponse.CoreResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = coreEmbed(coreResponse.type),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = coreResponse.buttonSet,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        coreResponse: BotResponse.CoreResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = coreEmbed(coreResponse.type),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = coreResponse.buttonSet,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        modulesResponse: BotResponse.ModulesResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = modulesEmbed(modulesResponse, getBotFeatureInfoUseCase.invoke()),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        modulesResponse: BotResponse.ModulesResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = modulesEmbed(modulesResponse, getBotFeatureInfoUseCase.invoke()),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = null,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        aliasResponse: BotResponse.AliasResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            message = message,
            embedBuilder = aliasResponseEmbed(aliasResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = (aliasResponse as? BotResponse.AliasResponse.GamePrompt)?.buttonSet,
        )
        return result
    }

    suspend fun respond(
        interaction: GuildChatInputCommandInteraction,
        aliasResponse: BotResponse.AliasResponse,
    ): EmptyResult<BotError> {
        val result = respond(
            interaction = interaction,
            embedBuilder = aliasResponseEmbed(aliasResponse),
            imageList = emptyList(),
            isExpanded = false,
            buttonSet = (aliasResponse as? BotResponse.AliasResponse.GamePrompt)?.buttonSet,
        )
        return result
    }

    suspend fun respond(
        message: Message,
        plainText: BotResponse.PlainText,
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
        plainText: BotResponse.PlainText,
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

    private fun MessageBuilder.textContent(plainText: BotResponse.PlainText) {
        content = plainText.text
        plainText.buttonSet?.let { discordButtonBuilder.createResponseButtons(messageBuilder = this, buttonSet = it) }
    }

    private fun coreEmbed(type: BotResponse.CoreResponse.Type): EmbedBuilder.() -> Unit {
        val featureInfo = getBotFeatureInfoUseCase.invoke()
        val embedBuilder = when (type) {
            BotResponse.CoreResponse.Type.Tip -> tipEmbed(featureInfo)
            BotResponse.CoreResponse.Type.Help -> helpEmbed(commandRegistry, featureInfo)
            BotResponse.CoreResponse.Type.Commands -> commandsEmbed(
                commandList = Command.entries.sortedBy { it.name },
                commandRegistry = commandRegistry,
                featureInfo = featureInfo,
            )
        }
        return embedBuilder
    }

    private fun aliasResponseEmbed(aliasResponse: BotResponse.AliasResponse): EmbedBuilder.() -> Unit {
        val embedBuilder = when (aliasResponse) {
            is BotResponse.AliasResponse.CharacterAliases -> aliasEmbed(aliasResponse.characterList)
            is BotResponse.AliasResponse.GamePrompt -> aliasGamePromptEmbed(
                gameList = aliasResponse.gameList,
                featureInfo = getBotFeatureInfoUseCase.invoke(),
            )
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
            createPromoEmbedUseCase.invoke(channel)
        }
    }


    private companion object {
        const val HTTP_FORBIDDEN = 403
        const val YELLOW = 0x00FFC107
    }
}
