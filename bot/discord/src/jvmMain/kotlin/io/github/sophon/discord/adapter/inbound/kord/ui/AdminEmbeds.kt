package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.toFormattedString
import io.github.sophon.discord.app.domain.model.BotResponse

/**
 * Title is the author's handle, so admins can copy it into `/reply`.
 */
internal fun feedbackEmbed(
    feedback: BotResponse.Feedback,
    featureInfo: FeatureInfo,
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

        featureFooter(featureInfo)
    }
    return embedBuilder
}

internal fun replyEmbed(
    reply: BotResponse.Reply,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit {
    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "Feedback response"
        color = Color(YELLOW)

        mandatoryField(
            name = "",
            value = reply.message,
            inline = false,
        )

        featureFooter(featureInfo)
    }
    return embedBuilder
}

internal fun banEmbed(
    ban: BotResponse.Ban,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit {
    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "Chat shit, get banged! 🔥🔥🔥"
        color = Color(YELLOW)

        mandatoryField(
            name = "User",
            value = "<@${ban.offenderId}>",
            inline = false,
        )

        mandatoryField(
            name = "Ban status",
            value = "BANNED 🔨: ${ban.bannedAt.toFormattedString()} → ${ban.expiresAt.toFormattedString()}",
            inline = false,
        )

        featureFooter(featureInfo)
    }
    return embedBuilder
}

internal fun unbanEmbed(
    unban: BotResponse.Unban,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit {
    val embedBuilder: EmbedBuilder.() -> Unit = {
        title = "Free like a bird! 🕊🕊🕊"
        color = Color(YELLOW)

        mandatoryField(
            name = "User",
            value = "<@${unban.offenderId}>",
            inline = false,
        )

        mandatoryField(
            name = "Ban status",
            value = "UNBANNED",
            inline = false,
        )

        featureFooter(featureInfo)
    }
    return embedBuilder
}


private const val YELLOW = 0x00FFC107
