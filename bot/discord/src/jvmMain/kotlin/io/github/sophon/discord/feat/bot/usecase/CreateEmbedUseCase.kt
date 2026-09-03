package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.Color
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.DiscordButtonBuilder
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.integration.model.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * USED FOR: general and error embeds
 */
@ExcludeFromCoverage("UI")
@OptIn(ExperimentalUuidApi::class)
internal class CreateEmbedUseCase(
    private val discordButtonBuilder: DiscordButtonBuilder,
) {
    suspend fun invoke(
        message: Message,
        embedBuilder: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        source: Source,
        fullEmbed: (EmbedBuilder.() -> Unit)? = null,
        imageList: BotOutput.Images? = null,
        buttons: BotOutput.ButtonSet? = null,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.random()
            val sentMessage = message.channel.createMessage {
                messageReference = message.id
                allowedMentions { repliedUser = false }

                embed(fullEmbed ?: embedBuilder)

                if (buttons?.buttonList.isNullOrEmpty().not()) {
                    discordButtonBuilder.createEmbedButtons(
                        messageBuilder = this,
                        buttonList = buttons.buttonList,
                        uuid = uuid,
                    )
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
                if (buttons.duration != EMBED_BUTTON_DURATION_INF.seconds) {
                    coroutineScope.launch {
                        delay(duration)
                        runCatching {
                            sentMessage.edit { components = mutableListOf() }
                        }.onFailure { error ->
                            Napier.e(tag = TAG) { "${source.serverName}: ${error.message}" }
                        }
                    }
                }
            }

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            when (e.status.code) {
                403 -> {
                    createMissingPermissionsEmbed(message = message, errorMessage = e.message)
                }
                else -> {}
            }
            Result.Error(BotError.Kord("${source.serverName}: ${e.toString()}"))
        }
    }

    suspend fun invoke(
        interaction: GuildChatInputCommandInteraction,
        embedBuilder: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        source: Source,
        imageList: BotOutput.Images? = null,
        buttons: BotOutput.ButtonSet? = null,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.random()

            interaction.respondPublic {
                respond(
                    uuid = uuid,
                    primaryEmbed = embedBuilder,
                    coroutineScope = coroutineScope,
                    source = source,
                    interaction = interaction,
                    buttons = buttons,
                    imageList = imageList,
                )
            }

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            when (e.status.code) {
                403 -> {
                    createMissingPermissionsEmbed(interaction = interaction, errorMessage = e.message)
                }
                else -> {}
            }

            Result.Error(BotError.Kord("${source.serverName}: ${e.toString()}"))
        }
    }


    private fun InteractionResponseCreateBuilder.respond(
        uuid: Uuid,
        primaryEmbed: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        source: Source,
        interaction: GuildChatInputCommandInteraction,
        buttons: BotOutput.ButtonSet? = null,
        imageList: BotOutput.Images? = null,
    ) {
        embed(primaryEmbed)

        if (buttons?.buttonList.isNullOrEmpty().not()) {
            discordButtonBuilder.createEmbedButtons(
                messageBuilder = this,
                buttonList = buttons.buttonList,
                uuid = uuid,
            )

            if (buttons.duration != EMBED_BUTTON_DURATION_INF.seconds) {
                coroutineScope.launch {
                    delay(buttons.duration)
                    runCatching {
                        interaction.getOriginalInteractionResponse().edit {
                            components = mutableListOf()
                        }
                    }.onFailure { error ->
                        Napier.e(tag = TAG) { "${source.serverName}: ${error.message}" }
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

    private suspend fun createMissingPermissionsEmbed(
        message: Message? = null,
        interaction: GuildChatInputCommandInteraction? = null,
        errorMessage: String?,
    ) {
        val messageEmbed: EmbedBuilder.() -> Unit = {
            title = "⚠️ Error"
            color = Color(YELLOW)
            mandatoryField(
                name = "",
                value = errorMessage,
            )
        }

        message?.channel?.createMessage {
            embed(messageEmbed)
        } ?: interaction?.respondPublic {
            embed(messageEmbed)
        }
    }


    private companion object {
        const val TAG = "CreateEmbedUseCase"
        const val YELLOW = 0x00FFC107
    }
}
