package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.ButtonInteraction
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.adapter.inbound.kord.ui.characterEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.moveEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.moveListEmbed
import io.github.sophon.discord.app.model.discord.ButtonEvent
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.model.response.CoreResponse
import io.github.sophon.discord.app.model.response.ListResponse
import io.github.sophon.discord.app.model.response.MediaResponse
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.util.kordRestCall
import io.github.sophon.discord.inPort.ProcessButtonEventUseCase

internal class DiscordButtonHandler(
    private val kordResponder: KordResponder,
    private val processButtonEventUseCase: ProcessButtonEventUseCase,
) {
    suspend fun process(interaction: ButtonInteraction) {
        val buttonEvent = decodeToButtonEvent(
            buttonId = interaction.componentId,
            sourceChannelId = interaction.message.channelId.toString(),
            sourceMessageId = interaction.message.id.toString(),
        )
        if (buttonEvent == null) {
            Napier.w(tag = TAG) { "Unknown button: ${interaction.componentId}" }
            return
        }

        kordRestCall(TAG) {
            val deferredResponse = interaction.deferPublicMessageUpdate()
            respondToButtonEvent(
                interaction = interaction,
                buttonEvent = buttonEvent,
                deferredResponse = deferredResponse,
            )
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private suspend fun respondToButtonEvent(
        interaction: ButtonInteraction,
        buttonEvent: ButtonEvent,
        deferredResponse: PublicMessageInteractionResponseBehavior,
    ) {
        kordRestCall(
            tag = TAG,
            onForbidden = { errorMessage ->
                kordResponder.whisper(response = deferredResponse, errorMessage = errorMessage)
            },
        ) {
            processButtonEventUseCase(buttonEvent)
                .onSuccess { response ->
                    when (buttonEvent) {
                        is ButtonEvent.Expand -> {
                            if (response is MoveResponse) {
                                kordResponder.edit(
                                    message = interaction.message,
                                    embedBuilder = moveEmbed(response),
                                    imageList = response.hitboxImageList,
                                    isExpanded = response.forceExpand,
                                    buttonSet = response.buttonSet,
                                )
                            }
                        }

                        is ButtonEvent.Query -> {
                            if (response is MoveResponse) {
                                kordResponder.respond(
                                    message = interaction.message,
                                    embedBuilder = moveEmbed(response),
                                    imageList = response.hitboxImageList,
                                    isExpanded = response.forceExpand,
                                    buttonSet = response.buttonSet,
                                    mentionedUser = interaction.user,
                                )
                            }
                        }

                        is ButtonEvent.Command -> {
                            when (response) {
                                is MoveResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        embedBuilder = moveEmbed(response),
                                        imageList = response.hitboxImageList,
                                        isExpanded = response.forceExpand,
                                        buttonSet = response.buttonSet,
                                        mentionedUser = interaction.user,
                                    )
                                }

                                is ListResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        embedBuilder = moveListEmbed(response),
                                        imageList = emptyList(),
                                        isExpanded = false,
                                        buttonSet = response.buttonSet,
                                        mentionedUser = interaction.user,
                                    )
                                }

                                is CharacterResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        embedBuilder = characterEmbed(response),
                                        imageList = emptyList(),
                                        isExpanded = false,
                                        buttonSet = null,
                                        mentionedUser = interaction.user,
                                    )
                                }

                                is CoreResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        coreResponse = response,
                                        mentionedUser = interaction.user,
                                    )
                                }

                                is ModulesResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        modulesResponse = response,
                                        mentionedUser = interaction.user,
                                    )
                                }

                                is AliasResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        aliasResponse = response,
                                        mentionedUser = interaction.user,
                                    )
                                }

                                is MediaResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        mediaResponse = response,
                                        mentionedUser = interaction.user,
                                    )
                                }

                                else -> {}
                            }
                        }

                        is ButtonEvent.Text -> {
                            if (response is PlainTextResponse) {
                                kordResponder.respondText(
                                    response = deferredResponse,
                                    mention = interaction.user.mention,
                                    text = response.text,
                                )
                            }
                        }

                        // already posted by the service
                        is ButtonEvent.Forward -> {}
                    }
                }
                .onError { error -> Napier.e(tag = TAG) { "Button event failed: $error" } }
        }
    }


    private companion object {
        const val TAG = "DiscordButtonHandler"
    }
}
