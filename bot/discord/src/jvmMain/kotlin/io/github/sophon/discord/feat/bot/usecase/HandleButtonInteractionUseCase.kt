package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestException
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.DiscordButtonBuilder
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.DiscordButton
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.integration.model.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class HandleButtonInteractionUseCase(
    private val routeCommandToFeatureUseCase: RouteCommandToFeatureUseCase,
    private val discordButtonBuilder: DiscordButtonBuilder,
) {
    suspend fun invoke(
        interaction: ButtonInteraction,
        editableEmbedMap: MutableMap<String, BotOutput>,
        coroutineScope: CoroutineScope,
    ): EmptyResult<BotError> {
        val source = Source(
            username = interaction.user.username,
            id = interaction.user.data.id.toString(),
            channelId = interaction.channelId.toString(),
        )
        val message = interaction.data.message.value?.let {
            interaction.message
        }

        return when(val button = discordButtonBuilder.decodeToDomainModel(buttonId = interaction.componentId)) {
            is DiscordButton.Query -> {
                val response = interaction.deferPublicResponse()
                query(interaction, response, button.query, source, coroutineScope)
                    .onError { error ->
                        response.respond {
                            embed {
                                title = "Interaction Failed"
                                description = error.toString()
                                color = Color(0x00FF0000)
                            }
                        }
                    }
            }
            is DiscordButton.Edit -> {
                interaction.deferPublicMessageUpdate()
                edit(button.messageId, message, editableEmbedMap)
            }
            is DiscordButton.Redirect -> {
                interaction.deferPublicMessageUpdate()
                redirect(button.channelId, message)
            }
            else -> {
                Result.Error(BotError.BotLogicError("Invalid button action"))
            }
        }
    }


    private suspend fun query(
        interaction: ButtonInteraction,
        response: DeferredPublicMessageInteractionResponseBehavior,
        query: String,
        source: Source,
        coroutineScope: CoroutineScope,
    ): EmptyResult<BotError> {
        return routeCommandToFeatureUseCase.invoke(source, query)
            .map { botOutput ->
                val uuid = Uuid.random()

                response.respond {
                    content = interaction.user.mention
                    botOutput.primaryEmbedBuilder?.let { embed(it) }

                    botOutput.buttons?.let { buttonSet ->
                        if (buttonSet.buttonList.isEmpty().not()) {
                            discordButtonBuilder.createEmbedButtons(
                                messageBuilder = this,
                                buttonList = buttonSet.buttonList,
                                uuid = uuid,
                            )

                            if (buttonSet.duration != EMBED_BUTTON_DURATION_INF.seconds) {
                                coroutineScope.launch {
                                    delay(buttonSet.duration)
                                    interaction.getOriginalInteractionResponse().edit {
                                        components = mutableListOf()
                                    }
                                }
                            }
                        }
                    }
                }

                Unit
            }
            .mapError { error ->
                response.respond {
                    embed {
                        title = "Interaction Failed"
                        description = error.toString()
                        color = Color(0x00FF0000)
                    }
                }

                BotError.Kord(error.toString())
            }
    }

    @Suppress("NestedBlockDepth")
    private suspend fun edit(
        messageId: String,
        message: Message?,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ): EmptyResult<BotError> {
        return try {
            if (message == null) {
                Result.Error(BotError.BotLogicError("Button has no data"))
            } else {
                message.apply {
                    val botOutput = editableEmbedMap[messageId]
                    botOutput?.mutableEmbedBuilder?.manualEditBuilder?.let { embedBuilder ->
                        edit {
                            embeds?.clear()
                            embed(embedBuilder)
                            editableEmbedMap.remove(messageId)
                            components = mutableListOf() //removes buttons
                            botOutput.images?.urls?.forEach { url ->
                                embed {
                                    title = botOutput.images.title
                                    this.url = botOutput.images.titleUrl
                                    image = url
                                }
                            }
                        }
                    }
                }
            }

            Result.Success(Unit)
        } catch (e: KtorRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        } catch (e: Exception) {
            Result.Error(BotError.Unknown(e.toString()))
        }
    }

    private suspend fun redirect(
        channelId: String,
        message: Message?,
    ): EmptyResult<BotError> {
        return try {
            if (message == null) {
                Result.Error(BotError.BotLogicError("Button has no data"))
            } else {
                val id = Snowflake(channelId)
                val targetChannel = message.kord.getChannelOf<TextChannel>(id)
                    ?: return Result.Error(BotError.BotLogicError("Channel not found: $channelId"))

                val embed = message.embeds.firstOrNull()
                    ?: return Result.Error(BotError.BotLogicError("Message has no embed"))

                targetChannel.createEmbed {
                    title = embed.title
                    color = embed.color
                    embed.fields.firstOrNull()?.let {
                        mandatoryField(name = it.name, value = it.value, inline = (it.inline == true))
                    }
                    embed.footer?.let {
                        footer {
                            text = it.text
                            icon = it.iconUrl
                        }
                    }
                }

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(BotError.Unknown(e.toString()))
        }
    }
}