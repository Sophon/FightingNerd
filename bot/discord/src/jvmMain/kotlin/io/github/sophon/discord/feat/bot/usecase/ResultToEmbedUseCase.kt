package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.TIME_AUTO_EDIT_EMBED_S
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.domain.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * TODO: we shouldn't be using embedBuilders here, we should receive the embed already
 * so extract the CreateErrorEmbedBuilderUseCase out
 */
internal class ResultToEmbedUseCase(
    private val createErrorEmbedBuilderUseCase: CreateErrorEmbedBuilderUseCase,
    private val createPlainMessageUseCase: CreatePlainMessageUseCase,
    private val createEmbedUseCase: CreateEmbedUseCase,
    private val createFeedbackEmbedUseCase: CreateFeedbackEmbedUseCase,
    private val createReplyEmbedUseCase: CreateReplyEmbedUseCase,
    private val createMutableEmbedUseCase: CreateMutableEmbedUseCase,
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
                val (embedBuilder, buttons) = createErrorEmbedBuilderUseCase.invoke(result.error)
                BotOutput(
                    mutableEmbedBuilder = embedBuilder,
                    buttons = buttons,
                )
            }
        }

        when {
            botOutput.primaryEmbedBuilder != null -> {
                with (createEmbedUseCase) {
                    invoke(
                        embedBuilder = botOutput.primaryEmbedBuilder,
                        coroutineScope = coroutineScope,
                        source = source,
                        imageList = botOutput.images,
                        buttons = botOutput.buttons,
                    ).onError { Napier.e(tag = TAG) { "embed: $it" } }
                }
            }
            botOutput.mutableEmbedBuilder != null -> {
                with (createMutableEmbedUseCase) {
                    invoke(
                        mutableEmbedBuilder = botOutput.mutableEmbedBuilder,
                        coroutineScope = coroutineScope,
                        buttons = botOutput.buttons,
                        editAfter = TIME_AUTO_EDIT_EMBED_S.seconds, //TODO: DON'T ALWAYS AUTO EDIT
                    )
                        .onSuccess { uuid ->
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
            botOutput.feedback != null -> {
                with (createFeedbackEmbedUseCase) {
                    invoke(botOutput.feedback, botOutput.buttons)
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
                val (errorEmbed, buttons) = createErrorEmbedBuilderUseCase.invoke(result.error)
                BotOutput(mutableEmbedBuilder = errorEmbed, buttons = buttons)
            }
        }

        when {
            botOutput.primaryEmbedBuilder != null -> {
                with (createEmbedUseCase) {
                    invoke(
                        embedBuilder = botOutput.primaryEmbedBuilder,
                        coroutineScope = coroutineScope,
                        source = source,
                        imageList = botOutput.images,
                        buttons = botOutput.buttons,
                    ).onError { Napier.e(tag = TAG) { "embed: $it" } }
                }
            }
            botOutput.mutableEmbedBuilder != null -> {
                with (createMutableEmbedUseCase) {
                    invoke(
                        mutableEmbedBuilder = botOutput.mutableEmbedBuilder,
                        coroutineScope = coroutineScope,
                        imageList = botOutput.images,
                        buttons = botOutput.buttons,
                    )
                        .onSuccess { uuid ->
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
            botOutput.feedback != null -> {
                with (createFeedbackEmbedUseCase) {
                    invoke(botOutput.feedback, botOutput.buttons)
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