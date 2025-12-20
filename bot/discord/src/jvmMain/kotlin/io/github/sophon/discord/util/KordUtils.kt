package io.github.sophon.discord.util

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
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
    imageList: BotOutput.Images? = null,
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
    embedBuilder: EmbedBuilder.() -> Unit,
    imageList: BotOutput.Images? = null,
): PublicInteractionResponseBehavior {
    return interaction.respondPublic {
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

internal suspend fun MessageCreateEvent.createEmbedMessage(
    feedback: BotOutput.Feedback,
): Message {
    feedback.feedbackChannelList.forEach { channelId ->
        val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))

        channel?.createMessage {
            embed(feedback.embedBuilder)
        }
    }

    return message.channel.createMessage {
        content = "Feedback sent successfully!"
    }
}

internal suspend fun MessageCreateEvent.createEmbedMessage(
    reply: BotOutput.Reply,
): Message {
    val channel = kord.getChannelOf<MessageChannel>(Snowflake(reply.recipient.channelId))

    channel?.createMessage {
        content = "<@${reply.recipient.id}>"
        embed(reply.embedBuilder)
    }

    return message.channel.createMessage {
        content = if (channel == null) "Failed to send" else "Reply sent successfully!"
    }
}

internal suspend fun GuildChatInputCommandInteractionCreateEvent.createEmbedResponse(
    feedback: BotOutput.Feedback,
): PublicInteractionResponseBehavior {
    feedback.feedbackChannelList.forEach { channelId ->
        val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))

        channel?.createMessage {
            embed(feedback.embedBuilder)
        }
    }

    return interaction.respondPublic {
        content = "Feedback sent successfully!"
    }
}

internal suspend fun GuildChatInputCommandInteractionCreateEvent.createEmbedResponse(
    reply: BotOutput.Reply,
): PublicInteractionResponseBehavior {
    val channel = kord.getChannelOf<MessageChannel>(Snowflake(reply.recipient.channelId))

    channel?.createMessage {
        content = "<@${reply.recipient.id}>"
        embed(reply.embedBuilder)
    }

    return interaction.respondPublic {
        content = if (channel == null) "Failed to send" else "Reply sent successfully!"
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
