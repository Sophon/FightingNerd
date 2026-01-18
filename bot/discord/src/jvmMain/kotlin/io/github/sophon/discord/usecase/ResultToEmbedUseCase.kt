package io.github.sophon.discord.usecase

import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.domain.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal class ResultToEmbedUseCase(
    private val createErrorEmbedUseCase: CreateErrorEmbedUseCase,
    private val createPlainMessageUseCase: CreatePlainMessageUseCase,
    private val createEmbedUseCase: CreateEmbedUseCase,
    private val createFeedbackEmbedUseCase: CreateFeedbackEmbedUseCase,
    private val createReplyEmbedUseCase: CreateReplyEmbedUseCase,
) {

    suspend fun MessageCreateEvent.invoke(
        source: Source,
        result: Result<BotOutput, BotError>,
        coroutineScope: CoroutineScope,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ) {
        val botOutput = when(result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Napier.e(tag = TAG) { "${result.error} in ${source.serverName}" }
                val (errorEmbed, buttons) = createErrorEmbedUseCase.invoke(result.error)
                BotOutput(errorEmbedBuilder = errorEmbed, buttons = buttons)
            }
        }

        when {
            botOutput.primaryEmbedBuilder != null -> {
                with (createEmbedUseCase) {
                    invoke(
                        primaryEmbed = botOutput.primaryEmbedBuilder,
                        coroutineScope = coroutineScope,
                        imageList = botOutput.images,
                        buttons = botOutput.buttons,
                    )
                        .onSuccess { uuid ->
                            botOutput.fullEmbedBuilder?.let {
                                botOutput.buttons?.duration?.let { duration ->
                                    if (duration != EMBED_BUTTON_DURATION_INF.seconds) {
                                        editableEmbedMap[uuid] = botOutput
                                        coroutineScope.launch {
                                            delay(duration)
                                            editableEmbedMap.remove(uuid)
                                        }
                                    }
                                }
                            }
                        }
                        .onError { Napier.e(tag = TAG) { "embed: $it" } }
                }
            }
            botOutput.plainText != null -> {
                with(createPlainMessageUseCase) {
                    invoke(botOutput.plainText).onError {
                        Napier.e(tag = TAG) { "handleMessage: $it" }
                    }
                }
            }
            botOutput.errorEmbedBuilder != null -> {
                with (createEmbedUseCase) {
                    invoke(
                        primaryEmbed = botOutput.errorEmbedBuilder,
                        coroutineScope = coroutineScope,
                        buttons = botOutput.buttons,
                    ).onError { Napier.e(tag = TAG) { "embed: $it" } }
                }
            }
            botOutput.feedback != null -> {
                with (createFeedbackEmbedUseCase) {
                    invoke(botOutput.feedback)
                }
            }
            botOutput.reply != null -> {
                with (createReplyEmbedUseCase) {
                    invoke(botOutput.reply)
                }
            }
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.invoke(
        source: Source,
        result: Result<BotOutput, BotError>,
        coroutineScope: CoroutineScope,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ) {
        val botOutput = when(result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Napier.e(tag = TAG) { "${result.error} in ${source.serverName}" }
                val (errorEmbed, buttons) = createErrorEmbedUseCase.invoke(result.error)
                BotOutput(errorEmbedBuilder = errorEmbed, buttons = buttons)
            }
        }

        when {
            botOutput.primaryEmbedBuilder != null -> {
                with (createEmbedUseCase) {
                    invoke(
                        primaryEmbed = botOutput.primaryEmbedBuilder,
                        coroutineScope = coroutineScope,
                        imageList = botOutput.images,
                        buttons = botOutput.buttons,
                    )
                        .onSuccess { uuid ->
                            botOutput.fullEmbedBuilder?.let {
                                botOutput.buttons?.duration?.let { duration ->
                                    if (duration != EMBED_BUTTON_DURATION_INF.seconds) {
                                        editableEmbedMap[uuid] = botOutput
                                        coroutineScope.launch {
                                            delay(duration)
                                            editableEmbedMap.remove(uuid)
                                        }
                                    }
                                }
                            }
                        }
                        .onError { Napier.e(tag = TAG) { "embed: $it" } }
                }
            }
            botOutput.plainText != null -> {
                with(createPlainMessageUseCase) {
                    invoke(botOutput.plainText).onError {
                        Napier.e(tag = TAG) { "handleMessage: $it" }
                    }
                }
            }
            botOutput.errorEmbedBuilder != null -> {
                with (createEmbedUseCase) {
                    invoke(
                        primaryEmbed = botOutput.errorEmbedBuilder,
                        coroutineScope = coroutineScope,
                        buttons = botOutput.buttons,
                    ).onError { Napier.e(tag = TAG) { "embed: $it" } }
                }
            }
            botOutput.feedback != null -> {
                with (createFeedbackEmbedUseCase) {
                    invoke(botOutput.feedback)
                }
            }
            botOutput.reply != null -> {
                with (createReplyEmbedUseCase) {
                    invoke(botOutput.reply)
                }
            }
        }
    }


    private companion object {
        const val TAG = "ResultToEmbedUseCase"
    }
}