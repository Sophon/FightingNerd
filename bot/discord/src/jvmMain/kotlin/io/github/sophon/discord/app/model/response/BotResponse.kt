package io.github.sophon.discord.app.model.response

import io.github.sophon.discord.EMBED_BUTTON_DURATION_DEFAULT_S
import io.github.sophon.discord.app.model.MoveId
import io.github.sophon.wiki.model.wiki.Game
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface BotResponse {
    val game: Game? get() = null


    data class DataSource(
        val name: String,
        val iconUrl: String,
        val color: Int,
    )

    data class Field(
        val title: String,
        val value: String,
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
            data class Query(val moveId: MoveId): Action()
            data object Edit : Action()
            data class Url(val url: String): Action()
            data class Redirect(val channelId: String): Action()
            data class Text(val text: String): Action()
            data class Expand(val moveId: MoveId): Action()
            data class Command(
                val command: io.github.sophon.discord.app.model.Command,
                val query: String,
            ): Action()
        }
    }
}
