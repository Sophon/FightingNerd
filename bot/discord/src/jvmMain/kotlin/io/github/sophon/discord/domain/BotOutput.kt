package io.github.sophon.discord.domain

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.discord.EMBED_BUTTON_DURATION_DEFAULT_S
import io.github.sophon.domain.Source
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class BotOutput(
    val primaryEmbedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val fullEmbedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val errorEmbedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val plainText: String? = null,
    val images: Images? = null,
    val feedback: Feedback? = null,
    val reply: Reply? = null,
    val buttons: ButtonSet? = null,
) {
    data class Images(
        val title: String,
        val titleUrl: String?,
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

    data class ButtonSet(
        val buttonList: List<EmbedButton> = listOf(),
        val duration: Duration = EMBED_BUTTON_DURATION_DEFAULT_S.seconds,
    )

    data class EmbedButton(
        val label: String,
        val action: Action,
    ) {
        sealed class Action {
            class Query(val query: String): Action()
            class Edit(): Action()
        }
    }
}