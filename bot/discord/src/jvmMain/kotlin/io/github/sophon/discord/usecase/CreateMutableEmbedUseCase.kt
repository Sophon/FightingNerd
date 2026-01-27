package io.github.sophon.discord.usecase

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.domain.Result
import io.github.sophon.core.util.rollChance
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.TIME_AUTO_EDIT_EMBED_S
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.BotOutput.ButtonSet
import io.github.sophon.discord.util.createButtons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateMutableEmbedUseCase {
    suspend fun MessageCreateEvent.invoke(
        primaryEmbedBuilder: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        autoEditEmbedBuilder: (EmbedBuilder.() -> Unit)? = null,
        buttons: ButtonSet? = null,
        editAfter: Duration = TIME_AUTO_EDIT_EMBED_S.seconds,
        deleteAfter: Duration? = null,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.random()

            val message = message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }

                embed(primaryEmbedBuilder)

                if (buttons?.buttonList.isNullOrEmpty().not()) {
                    createButtons(uuid, buttons.buttonList)
                }
            }

            coroutineScope.launch {
                delay(editAfter)
                message.edit {
                    components = mutableListOf()
                    autoEditEmbedBuilder?.let { builder ->
                        embed(builder)
                    }
                }
            }

            deleteAfter?.let { duration ->
                coroutineScope.launch {
                    delay(duration)
                    message.delete()
                }
            }

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.invoke(
        editableEmbedBuilder: BotOutput.EditableEmbedBuilder,
        coroutineScope: CoroutineScope,
        imageList: BotOutput.Images? = null,
        buttons: ButtonSet? = null,
        editAfter: Duration = TIME_AUTO_EDIT_EMBED_S.seconds,
        deleteAfter: Duration? = null,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.random()

            interaction.respondPublic {
                embed(editableEmbedBuilder.primaryBuilder)

                if (buttons?.buttonList.isNullOrEmpty().not()) {
                    createButtons(uuid, buttons.buttonList)

                    if (buttons.duration != EMBED_BUTTON_DURATION_INF.seconds) {
                        coroutineScope.launch {
                            delay(buttons.duration)
                            interaction.getOriginalInteractionResponse().edit {
                                components = mutableListOf()
                            }
                        }
                    }
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

            coroutineScope.launch {
                delay(editAfter)
                interaction.getOriginalInteractionResponseOrNull()?.edit {
                    components = mutableListOf()
                    editableEmbedBuilder.autoEditBuilder?.let { builder ->
                        embeds = mutableListOf(EmbedBuilder().apply(builder))
                    }
                }
            }

            deleteAfter?.let { duration ->
                coroutineScope.launch {
                    delay(duration)
                    interaction.getOriginalInteractionResponseOrNull()?.delete()
                }
            }

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }
}