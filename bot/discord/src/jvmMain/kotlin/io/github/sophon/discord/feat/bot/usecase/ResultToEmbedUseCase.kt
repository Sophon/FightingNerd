package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.TIME_AUTO_EDIT_EMBED_S
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.integration.model.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * TODO: we shouldn't be using embedBuilders here, we should receive the embed already
 * so extract the CreateErrorEmbedBuilderUseCase out
 */
@ExcludeFromCoverage("UI")
internal class ResultToEmbedUseCase(
    private val createErrorEmbedBuilderUseCase: CreateErrorEmbedBuilderUseCase,
    private val createPlainMessageUseCase: CreatePlainMessageUseCase,
    private val createEmbedUseCase: CreateEmbedUseCase,
    private val createFeedbackEmbedUseCase: CreateFeedbackEmbedUseCase,
    private val createReplyEmbedUseCase: CreateReplyEmbedUseCase,
    private val createMutableEmbedUseCase: CreateMutableEmbedUseCase,
) {
    suspend fun invoke(
        message: Message,
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
                createEmbedUseCase.invoke(
                    message = message,
                    embedBuilder = botOutput.primaryEmbedBuilder,
                    coroutineScope = coroutineScope,
                    source = source,
                    imageList = botOutput.images,
                    buttons = botOutput.buttons,
                ).onError { Napier.e(tag = TAG) { "embed: $it" } }
            }
            botOutput.mutableEmbedBuilder != null -> {
                createMutableEmbedUseCase.invoke(
                    message = message,
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
            botOutput.plainText != null -> {
                createPlainMessageUseCase.invoke(message, botOutput.plainText).onError {
                    Napier.e(tag = TAG) { "handleMessage: $it" }
                }
            }
            botOutput.feedback != null -> {
                createFeedbackEmbedUseCase.invoke(message, botOutput.feedback, botOutput.buttons)
            }
            botOutput.reply != null -> {
                createReplyEmbedUseCase.invoke(message, botOutput.reply)
            }
        }
    }

    suspend fun invoke(
        interaction: GuildChatInputCommandInteraction,
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
                createEmbedUseCase.invoke(
                    interaction = interaction,
                    embedBuilder = botOutput.primaryEmbedBuilder,
                    coroutineScope = coroutineScope,
                    source = source,
                    imageList = botOutput.images,
                    buttons = botOutput.buttons,
                ).onError { Napier.e(tag = TAG) { "embed: $it" } }
            }
            botOutput.mutableEmbedBuilder != null -> {
                createMutableEmbedUseCase.invoke(
                    interaction = interaction,
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
            botOutput.plainText != null -> {
                createPlainMessageUseCase.invoke(interaction, botOutput.plainText).onError {
                    Napier.e(tag = TAG) { "handleMessage: $it" }
                }
            }
            botOutput.feedback != null -> {
                createFeedbackEmbedUseCase.invoke(interaction, botOutput.feedback, botOutput.buttons)
            }
            botOutput.reply != null -> {
                createReplyEmbedUseCase.invoke(interaction, botOutput.reply)
            }
        }
    }


    private companion object {
        const val TAG = "ResultToEmbedUseCase"
    }
}
