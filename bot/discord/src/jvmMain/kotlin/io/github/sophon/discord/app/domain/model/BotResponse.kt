package io.github.sophon.discord.app.domain.model

import io.github.sophon.core.util.toFormattedString
import io.github.sophon.discord.EMBED_BUTTON_DURATION_DEFAULT_S
import io.github.sophon.wiki.application.domain.model.wiki.Game
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

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
        val stance: String? = null,

        val forceExpand: Boolean = false,
        val buttonSet: ButtonSet? = null,
    ): BotResponse

    data class CharacterResponse(
        val id: String,
        val game: Game,
        val displayName: String,
        val url: String,
        val dataSource: DataSource,
        val aliasList: List<String> = emptyList(),
        val propertyList: List<Field> = emptyList(),
    ): BotResponse

    sealed interface AliasResponse: BotResponse {
        data class CharacterAliases(
            val characterList: List<CharacterResponse>,
        ): AliasResponse

        data class GamePrompt(
            val gameList: List<String>,
            val buttonSet: ButtonSet,
        ): AliasResponse
    }

    data class ListResponse(
        val title: String,
        val values: List<String>,
        val dataSource: DataSource,
        val buttonSet: ButtonSet? = null,
    ): BotResponse

    data class PlainText(
        val text: String,
        val buttonSet: ButtonSet? = null,
    ): BotResponse

    data class Error(val text: String): BotResponse

    data object Ignore: BotResponse

    data class CoreResponse(
        val type: Type,
        val buttonSet: ButtonSet? = null,
    ): BotResponse {
        enum class Type {
            Tip,
            Help,
            Commands,
        }
    }

    data class ModulesResponse(
        val moduleList: List<Module>,
    ): BotResponse {
        data class Module(
            val name: String,
            val url: String,
            val gameList: List<String>,
        )
    }

    data class SteamLobby(
        val hostName: String,
        val lobbyName: String?,
        val password: String?,
        val buttonSet: ButtonSet,
    ): BotResponse

    data class Ban(
        val offender: UserRequest.Source,
        val bannedAt: Instant,
        val expiresAt: Instant,
        val issuerId: String,
        val preventBotUsage: Boolean,
    ): BotResponse {
        override fun toString(): String {
            return "BANNED: ${bannedAt.toFormattedString()} → ${expiresAt.toFormattedString()}"
        }
    }

    data class Unban(
        val offender: UserRequest.Source,
    ): BotResponse

    data class Feedback(
        val author: UserRequest.Source,
        val message: String,
        val feedbackChannelIdList: List<String>,
        val buttonSet: ButtonSet,
    ): BotResponse

    data class Reply(
        val recipient: UserRequest.Source,
        val message: String,
    ): BotResponse

    data class Redirect(val channelId: String): BotResponse


    data class DataSource(
        val name: String,
        val iconUrl: String,
        val color: Int,
    )

    data class Field(
        val title: String,
        val value: String,
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
            data class Query(val moveId: MoveId): Action()
            data object Edit : Action()
            data class Url(val url: String): Action()
            data class Redirect(val channelId: String): Action()
            data class Text(val text: String): Action()
            data class Expand(val moveId: MoveId): Action()
            data class Command(
                val command: io.github.sophon.discord.app.domain.model.Command,
                val query: String,
            ): Action()
        }
    }
}
