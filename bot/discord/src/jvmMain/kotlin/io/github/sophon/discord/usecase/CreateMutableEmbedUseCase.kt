package io.github.sophon.discord.usecase

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.RestRequestException
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.BotOutput.ButtonSet
import io.github.sophon.discord.util.createButtons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateMutableEmbedUseCase {
    suspend fun MessageCreateEvent.invoke(
        primaryEmbedBuilder: EmbedBuilder.() -> Unit,
        editedEmbedBuilder: EmbedBuilder.() -> Unit,
        coroutineScope: CoroutineScope,
        buttons: ButtonSet? = null,
        editAfter: Duration? = null,
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

            editAfter?.let { duration ->
                coroutineScope.launch {
                    delay(duration)
                    message.edit {
                        embeds = mutableListOf()
                        components = mutableListOf()
                        embed(editedEmbedBuilder)
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
}