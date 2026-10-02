package io.github.sophon.discord.app.domain.model

import io.github.sophon.discord.EMBED_BUTTON_DURATION_DEFAULT_S
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface BotResponse {

    data class MoveResponse(
        val input: String,
        val url: String?,
        val characterName: String,
        val moveName: String?,
        val characterImageUrl: String?,
        val primaryFields: List<Field>,
        val dataSource: DataSource,
        val isCollapsedByDefault: Boolean = true,
        val secondaryFields: List<Field> = emptyList(),
        val aliasList: List<String> = emptyList(),
        val noteList: List<String> = emptyList(),
        val hitboxImageList: List<String> = emptyList(),

        val forceExpand: Boolean = false,
        val buttonSet: ButtonSet? = null,
    ): BotResponse {
        data class Field(
            val title: String,
            val value: String,
        )
    }

    data class ListResponse(
        val values: List<String>,
        val buttonSet: ButtonSet,
    ): BotResponse

    data class PlainText(
        val text: String,
        val dataSource: DataSource,
        val buttonSet: ButtonSet? = null,
    ): BotResponse

    data class Error(val text: String): BotResponse

    data object Ignore: BotResponse


    data class DataSource(
        val name: String,
        val iconUrl: String,
    )

    data class Images(
        val title: String,
        val titleUrl: String?,
        val urlList: List<String>,
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
            data class Query(val query: String): Action()
            data object Edit : Action()
            data class Url(val url: String): Action()
            data class Redirect(val channelId: String): Action()
            data class Text(val text: String): Action()
            data class Expand(val moveId: MoveId): Action()
        }
    }
}
