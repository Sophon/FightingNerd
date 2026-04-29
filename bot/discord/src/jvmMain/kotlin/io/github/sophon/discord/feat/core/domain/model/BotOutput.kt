package io.github.sophon.discord.feat.core.domain.model

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.discord.EMBED_BUTTON_DURATION_DEFAULT_S
import io.github.sophon.domain.Source
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class BotOutput(
    val primaryEmbedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val mutableEmbedBuilder: MutableEmbedBuilder? = null,
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
            class Url(val url: String): Action()
            class Redirect(val channelId: String): Action()
        }
    }

    /**
     * Editable Embeds always edit themselves after delay.
     * If `auto` is not null, it will edit itself into that.
     * `manual` is used when a button is pressed.
     */
    data class MutableEmbedBuilder(
        val primaryBuilder: (EmbedBuilder.() -> Unit),
        val autoEditBuilder: (EmbedBuilder.() -> Unit)? = null,
        val manualEditBuilder: (EmbedBuilder.() -> Unit)? = null,
    )
}