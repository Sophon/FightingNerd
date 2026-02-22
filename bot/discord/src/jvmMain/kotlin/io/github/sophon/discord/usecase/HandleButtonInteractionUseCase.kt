package io.github.sophon.discord.usecase

import dev.kord.common.Color
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.rest.builder.message.embed
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.usecase.CreateEmbedUseCase.Companion.KEY_EDIT
import io.github.sophon.discord.usecase.CreateEmbedUseCase.Companion.KEY_QUERY
import io.github.sophon.discord.usecase.CreateEmbedUseCase.Companion.KEY_REDIRECT
import io.github.sophon.discord.util.createButtons
import io.github.sophon.domain.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class HandleButtonInteractionUseCase(
    private val routeCommandToFeatureUseCase: RouteCommandToFeatureUseCase,
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
        val buttonActionData = interaction.componentId
        val message = interaction.data.message.value?.let {
            interaction.message
        }

        return when {
            (KEY_QUERY in buttonActionData) -> {
                val response = interaction.deferPublicResponse()
                val message = buttonActionData.substringAfter(KEY_QUERY)

                query(interaction, response, message, source, coroutineScope)
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
            (KEY_EDIT in buttonActionData) -> {
                interaction.deferPublicMessageUpdate()
                edit(buttonActionData, message, editableEmbedMap)
            }
            (KEY_REDIRECT in buttonActionData) -> {
                TODO("we need to decode data from the action")
            }
            else -> {
                Result.Error(BotError.BotLogicError("Invalid button action"))
            }
        }
    }


    private suspend fun query(
        interaction: ButtonInteraction,
        response: DeferredPublicMessageInteractionResponseBehavior,
        message: String,
        source: Source,
        coroutineScope: CoroutineScope,
    ): EmptyResult<BotError> {
        return routeCommandToFeatureUseCase.invoke(source, message)
            .map { botOutput ->
                val uuid = Uuid.random()

                response.respond {
                    botOutput.primaryEmbedBuilder?.let { embed(it) }

                    botOutput.buttons?.let { buttonSet ->
                        if (buttonSet.buttonList.isEmpty().not()) {
                            createButtons(buttonSet.buttonList, uuid)

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

    private suspend fun edit(
        buttonActionData: String,
        message: Message?,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ): EmptyResult<BotError> {
        return try {
            val uuid = buttonActionData.substringAfter(KEY_EDIT)

            if (message == null) {
                Result.Error(BotError.BotLogicError("Button has no data"))
            } else {
                message.apply {
                    val botOutput = editableEmbedMap[uuid]
                    botOutput?.mutableEmbedBuilder?.manualEditBuilder?.let { embedBuilder ->

                        edit {
                            embeds?.clear()
                            embed(embedBuilder)
                            editableEmbedMap.remove(uuid)
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
        } catch (e: Exception) {
            Result.Error(BotError.Unknown(e.toString()))
        }
    }
}