package io.github.sophon.discord.util

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.embed
import io.github.sophon.discord.domain.BotOutput

internal suspend fun MessageCreateEvent.createEmbedMessage(
    reply: BotOutput.Reply,
): Message {
    val channel = kord.getChannelOf<MessageChannel>(Snowflake(reply.target.channelId))

    channel?.createMessage {
        content = "<@${reply.target.id}>"
        embed(reply.embedBuilder)
    }

    return message.channel.createMessage {
        content = if (channel == null) "Failed to send" else "Reply sent successfully!"
    }
}

internal suspend fun GuildChatInputCommandInteractionCreateEvent.createEmbedResponse(
    reply: BotOutput.Reply,
): PublicInteractionResponseBehavior {
    val channel = kord.getChannelOf<MessageChannel>(Snowflake(reply.target.channelId))

    channel?.createMessage {
        content = "<@${reply.target.id}>"
        embed(reply.embedBuilder)
    }

    return interaction.respondPublic {
        content = if (channel == null) "Failed to send" else "Reply sent successfully!"
    }
}

