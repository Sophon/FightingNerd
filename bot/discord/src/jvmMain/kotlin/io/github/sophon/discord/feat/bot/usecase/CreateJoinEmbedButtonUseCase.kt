package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.Color
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_SCRIPT_LOBBY
import io.github.sophon.discord.domain.model.BotOutput
import io.github.sophon.discord.util.optionalField
import io.github.sophon.domain.Source
import kotlin.time.Duration.Companion.seconds

internal class CreateJoinEmbedButtonUseCase {
    fun invoke(
        origin: Source,
        query: String,
    ): Result<BotOutput, BotError> {
        val parts = query.split(" ")
        val steamLobbyUrl = parts[0]
        val password = parts.getOrNull(1)
        val lobbyName = if (parts.size > 2) {
            parts.drop(2).joinToString(" ")
        } else {
            null
        }

        if (steamLobbyUrl.startsWith("steam://joinlobby/").not())
            return Result.Error(BotError.InvalidSteamLobbyUrl(steamLobbyUrl))

        val url = "$URL_SCRIPT_LOBBY?target=$steamLobbyUrl"
        val userName = origin.username

        val botOutput = BotOutput(
            primaryEmbedBuilder = {
                title = "Join $userName's lobby!"
                optionalField(
                    name = "Lobby name",
                    value = password?.let {  "```$lobbyName```" },
                    inline = false,
                )
                optionalField(
                    name = "Password",
                    value = password?.let {  "```$password```" },
                    inline = false,
                )
                color = Color(PURPLE)
            },
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "🎮 JOIN",
                        action = BotOutput.EmbedButton.Action.Url(url)
                    )
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        )

        return Result.Success(botOutput)
    }


    private companion object {
        const val PURPLE = 0x00A020F0
    }
}