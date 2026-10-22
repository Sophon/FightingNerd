package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.util.toFormattedString
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.discord.app.model.response.FeedbackResponse
import io.github.sophon.discord.app.model.response.ReplyResponse
import io.github.sophon.discord.app.model.response.UnbanResponse

/**
 * Title is the author's handle, so admins can copy it into `/reply`.
 */
internal fun feedbackEmbed(
    feedback: FeedbackResponse,
): EmbedBuilder.() -> Unit {
    val author = feedback.author
    val origin = if (author.serverName.isBlank()) {
        author.username
    } else {
        "${author.username} from ${author.serverName}"
    }

    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "${author.username}-${author.id}-${author.channelId}"
        color = Color(YELLOW)

        mandatoryField(
            name = origin,
            value = feedback.message,
            inline = false,
        )

        featureFooter(feedback.dataSource)
    }
    return embedBuilder
}

internal fun replyEmbed(
    reply: ReplyResponse,
): EmbedBuilder.() -> Unit {
    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "Feedback response"
        color = Color(YELLOW)

        mandatoryField(
            name = "",
            value = reply.message,
            inline = false,
        )

        featureFooter(reply.dataSource)
    }
    return embedBuilder
}

internal fun banEmbed(
    ban: BanResponse,
): EmbedBuilder.() -> Unit {
    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "Chat shit, get banged! 🔥🔥🔥"
        color = Color(YELLOW)

        mandatoryField(
            name = "User",
            value = "<@${ban.offender.id}>",
            inline = false,
        )

        mandatoryField(
            name = "Ban status",
            value = "BANNED 🔨: ${ban.bannedAt.toFormattedString()} → ${ban.expiresAt.toFormattedString()}",
            inline = false,
        )

        featureFooter(ban.dataSource)
    }
    return embedBuilder
}

internal fun unbanEmbed(
    unban: UnbanResponse,
): EmbedBuilder.() -> Unit {
    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "Free like a bird! 🕊🕊🕊"
        color = Color(YELLOW)

        mandatoryField(
            name = "User",
            value = "<@${unban.offender.id}>",
            inline = false,
        )

        mandatoryField(
            name = "Ban status",
            value = "UNBANNED",
            inline = false,
        )

        featureFooter(unban.dataSource)
    }
    return embedBuilder
}


private const val YELLOW = 0x00FFC107
