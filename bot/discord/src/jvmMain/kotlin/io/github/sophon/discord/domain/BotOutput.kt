package io.github.sophon.discord.domain

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.domain.Source

data class BotOutput(
    val embedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val plainText: String? = null,
    val errorEmbedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val images: Images? = null,
    val feedback: Feedback? = null,
    val reply: Reply? = null,
) {
    data class Images(
        val title: String,
        val titleUrl: String,
        val urls: List<String>,
    )

    data class Feedback(
        val embedBuilder: (EmbedBuilder.() -> Unit),
        val origin: Source,
        val feedbackChannelList: List<String>,
    )

    data class Reply(
        val embedBuilder: (EmbedBuilder.() -> Unit),
        val target: Source,
    )
}