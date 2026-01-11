package io.github.sophon.discord.usecase

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondPublic
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
import io.github.sophon.discord.domain.BotOutput.ButtonSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * USED FOR: general and error embeds
 */
@OptIn(ExperimentalUuidApi::class)
internal class CreateEmbedUseCase {

    suspend fun MessageCreateEvent.invoke(
        primaryEmbed: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        fullEmbed: (EmbedBuilder.() -> Unit)? = null,
        imageList: BotOutput.Images? = null,
        buttons: ButtonSet? = null,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.random()
            val message = message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }

                embed(fullEmbed ?: primaryEmbed)

                if (buttons?.buttonList.isNullOrEmpty().not()) {
                    createButtons(uuid, buttons.buttonList)
                }

                imageList?.urls?.forEach { url ->
                    embed {
                        title = imageList.title
                        this.url = imageList.titleUrl
                        image = url
                    }
                }
            }

            buttons?.apply {
                coroutineScope.launch {
                    delay(duration)
                    message.edit {
                        components = mutableListOf()
                    }
                }
            }

            if (rollChance(successPercentage = 1)) {
                message.channel.createMessage {
                    content = "Consider donating (`/donate` or `/tip`): **<$URL_KOFI>**"
                }
            }

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.invoke(
        primaryEmbed: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        fullEmbed: (EmbedBuilder.() -> Unit)? = null,
        imageList: BotOutput.Images? = null,
        buttons: ButtonSet? = null,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.random()

            interaction.respondPublic {
                embed(fullEmbed ?: primaryEmbed)

                if (buttons?.buttonList.isNullOrEmpty().not()) {
                    createButtons(uuid, buttons.buttonList)

                    coroutineScope.launch {
                        delay(buttons.duration)
                        interaction.getOriginalInteractionResponse().edit {
                            components = mutableListOf()
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

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }


    private fun MessageBuilder.createButtons(
        uuid: Uuid,
        buttons: List<BotOutput.EmbedButton>
    ) {
        buttons.chunked(5).forEach { rowButtons ->
            actionRow {
                rowButtons.forEach { button ->
                    val action = when(button.action) {
                         is BotOutput.EmbedButton.Action.Query-> {
                             "$KEY_QUERY${button.action.query}"
                         }
                        is BotOutput.EmbedButton.Action.Edit -> "$KEY_EDIT$uuid"
                    }

                    interactionButton(
                        style = ButtonStyle.Primary,
                        customId = action,
                    ) {
                        label = button.label
                        disabled = false
                    }
                }
            }
        }
    }

    internal companion object {
        const val KEY_QUERY = "query: "
        const val KEY_EDIT = "edit: "
    }
}