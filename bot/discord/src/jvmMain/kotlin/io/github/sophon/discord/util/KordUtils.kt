package io.github.sophon.discord.util

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.embed
import io.github.sophon.discord.featureRegistry.BotOutput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal suspend fun MessageCreateEvent.createEmbedMessage(
    embedBuilder: EmbedBuilder.() -> Unit,
    imageList: BotOutput.Images?,
): Message {
    return message.channel.createMessage {
        messageReference = message.id
        allowedMentions { repliedUser = false }
        embed(embedBuilder)

        imageList?.urls?.forEach { url ->
            embed {
                title = imageList.title
                this.url = imageList.titleUrl
                image = url
            }
        }
    }
}

internal suspend fun MessageCreateEvent.createPlainMessage(
    text: String,
): Message {
    return message.channel.createMessage {
        messageReference = message.id
        allowedMentions { repliedUser = false }
        content = text
    }
}

internal suspend fun GuildChatInputCommandInteractionCreateEvent.createEmbedResponse(
    embedBuilder: EmbedBuilder.() -> Unit
): PublicInteractionResponseBehavior {
    return interaction.respondPublic {
        embed(embedBuilder)
    }
}

internal suspend fun GuildChatInputCommandInteractionCreateEvent.createPlainResponse(
    text: String
): PublicInteractionResponseBehavior {
    return interaction.respondPublic {
        content = text
    }
}

internal fun Message.delete(
    delay: Long,
    scope: CoroutineScope,
) {
    scope.launch {
        delay(delay)
        delete()
    }
}

internal suspend fun GuildChatInputCommandInteractionCreateEvent.deleteInteraction(
    delay: Long,
    scope: CoroutineScope
) {
    interaction.getOriginalInteractionResponse().delete(delay, scope)
}