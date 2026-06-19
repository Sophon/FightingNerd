package io.github.sophon.discord.feat.bot.usecase

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
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.rollChance
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.RNG_DONATION_PCT_COMMAND
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.feat.core.domain.DiscordButtonBuilder
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.util.donationMessage
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
@OptIn(ExperimentalUuidApi::class)
internal class CreateEmbedUseCase(
    private val discordButtonBuilder: DiscordButtonBuilder,
) {

    suspend fun MessageCreateEvent.invoke(
        embedBuilder: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        source: Source,
        fullEmbed: (EmbedBuilder.() -> Unit)? = null,
        imageList: BotOutput.Images? = null,
        buttons: BotOutput.ButtonSet? = null,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.Companion.random()
            val message = message.channel.createMessage {
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
                            message.edit { components = mutableListOf() }
                        }.onFailure { error ->
                            Napier.e(tag = TAG) { "${source.serverName}: ${error.message}" }
                        }
                    }
                }
            }

            if (rollChance(successPercentage = RNG_DONATION_PCT_COMMAND)) {
                message.channel.createMessage {
                    content = donationMessage()
                }
            }

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.invoke(
        embedBuilder: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        source: Source,
        imageList: BotOutput.Images? = null,
        buttons: BotOutput.ButtonSet? = null,
        isEphemeral: Boolean = false,
    ): Result<String, BotError> {
        return try {
            val uuid = Uuid.Companion.random()

            if (isEphemeral) {
                interaction.respondEphemeral {
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
            } else {
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
            }

            if (rollChance(successPercentage = RNG_DONATION_PCT_COMMAND)) {
                interaction.channel.createMessage {
                    content = donationMessage()
                }
            }

            Result.Success(uuid.toString())
        } catch (e: RestRequestException) {
            Result.Error(BotError.Kord(e.toString()))
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


    private companion object {
        const val TAG = "CreateEmbedUseCase"
    }
}